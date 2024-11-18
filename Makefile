JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
BINDIR = bin
SRCDIR = src
PACKAGEDIR = barScheduling

# Compile all .java files to .class files
$(BINDIR)/%.class: $(SRCDIR)/$(PACKAGEDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(SRCDIR) $<

# Define classes
CLASSES = Barman.class DrinkOrder.class Patron.class SchedulingSimulation.class

# Generate paths for class files
CLASS_FILES = $(CLASSES:%.class=$(BINDIR)/%.class)

# Default target
default: $(CLASS_FILES)

# Clean target
clean:
	rm -rf $(BINDIR)/barScheduling/*.class
	rm -rf *.txt

# First Come First Serve
FCFS: $(BINDIR)/SchedulingSimulation.class
	$(JAVA) -cp $(BINDIR) $(PACKAGEDIR).SchedulingSimulation "100" "0"


# Shortest Job First
SJF: $(BINDIR)/SchedulingSimulation.class
	$(JAVA) -cp $(BINDIR) $(PACKAGEDIR).SchedulingSimulation "10" "1"
