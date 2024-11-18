//M. M. Kuttel 2024 mkuttel@gmail.com
package barScheduling;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/*
 This is the basicclass, representing the patrons at the bar
 */

public class Patron extends Thread {
	
	private Random random = new Random();// for variation in Patron behaviour

	private CountDownLatch startSignal; //all start at once, actually shared
	private Barman theBarman; //the Barman is actually shared though

	private int ID; //thread ID  , orderId
	private int lengthOfOrder;
	private long startTime, endTime; //for all the metrics
	
	public static FileWriter fileW;
	
	// You will need to add static FileWriter objects for waiting time and response time in the class level
	public static FileWriter waitingTimeFile;
	public static FileWriter responseTimeFile;
	
	private DrinkOrder [] drinksOrder;
	
	
	Patron( int ID,  CountDownLatch startSignal, Barman aBarman) {
		this.ID = ID;
		this.startSignal = startSignal;
		this.theBarman = aBarman;
		this.lengthOfOrder = random.nextInt(5) + 1;//between 1 and 5 drinks
		drinksOrder= new DrinkOrder[lengthOfOrder];
	}
	
	public  void writeToFile(String data) throws IOException {
	    synchronized (fileW) {
	    	fileW.write(data);
	    }
	}
	
	public void writeResponseFile(String data) throws IOException {
		synchronized (responseTimeFile) {
			responseTimeFile.write(data);
		}
	}
	
	public void writeWaitingTimeFile(String data) throws IOException {
		synchronized (waitingTimeFile) {
			waitingTimeFile.write(data);
		}
	}
	
	
	
	public void run() {
		try {
			//Do NOT change the block of code below - this is the arrival times
			startSignal.countDown(); //this patron is ready
			startSignal.await(); //wait till everyone is ready
	        int arrivalTime = random.nextInt(300)+ID*100;  // patrons arrive gradually later
	        sleep(arrivalTime);// Patrons arrive at staggered  times depending on ID
			System.out.println("thirsty Patron "+ this.ID +" arrived");
			//END do not change
			
			// To get the first drink prepared
			long firstOrderTime = System.currentTimeMillis();
			AtomicLong firstResponseTime  = new AtomicLong(Long.MIN_VALUE);
            
            
            System.out.println("Patron "+ this.ID + " submitting order of " + lengthOfOrder +" drinks"); //output in standard format  - do not change this
			
			//create drinks order
			for (int i = 0; i<lengthOfOrder; i++) {
				drinksOrder[i] = new DrinkOrder(this.ID);
                drinksOrder[i].setStartingTime(System.currentTimeMillis());
				theBarman.placeDrinkOrder(drinksOrder[i]);
			}
			
			
			for (int i=0; i<lengthOfOrder; i++) {
				System.out.println("Order placed by " + drinksOrder[i].toString());
				drinksOrder[i].waitForOrder();
				if (firstResponseTime.get() == Long.MIN_VALUE) {
					firstResponseTime.set(System.currentTimeMillis());
				}
			}
			
			
			// TurnAround Time
			long totalTime = System.currentTimeMillis() - firstOrderTime;
			
			// Response Time
			long orderResponseTime = firstResponseTime.get() - firstOrderTime;
			
			// ThroughPut
			int totalCost = Arrays.stream(drinksOrder).mapToInt(DrinkOrder::getExecutionTime).sum();
			
			// waiting time = turnAroundTime  - BurstTime
            int totalExecutionTime = 0;
			for (DrinkOrder order : drinksOrder) {
				totalExecutionTime += order.getExecutionTime();
			}
			long totalWaitingTime = totalTime - totalExecutionTime;

			
			writeToFile( String.format("%d,%d,%d\n", ID, arrivalTime, totalTime));
			System.out.println("Patron "+ this.ID + " got order in " + totalTime);
			
			
			writeWaitingTimeFile(String.format("%d,%d\n", ID, totalWaitingTime));
			writeResponseFile(String.format("%d,%d\n", ID, orderResponseTime));
			
			System.out.println("Patron " + this.ID + " got all drinks in " + totalTime + " ms and first drink in " + orderResponseTime + " ms");
		} catch (InterruptedException e1) {  //do nothing
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
	

