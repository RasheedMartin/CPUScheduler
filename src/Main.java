import java.io.*;
import java.io.BufferedReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


class Process { // Class structure for the processes
    private int pid;
    private int arrivalTime;
    private int burstTime;
    private int burstTimeRemaining; // the amount of CPU time remaining after each execution
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private boolean isComplete;
    private boolean inQueue;
    private int responseTime;

    static int idleTime = 0;
    static int clock;


    public Process() {
        pid = 0;
        arrivalTime = 0;
        burstTime = 0;
        burstTimeRemaining = 0;
        completionTime = 0;
        turnaroundTime = 0;
        waitingTime = 0;
        isComplete = false;
        inQueue = false;
        responseTime = -1;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getBurstTimeRemaining() {
        return burstTimeRemaining;
    }

    public void setBurstTimeRemaining(int burstTimeRemaining) {
        this.burstTimeRemaining = burstTimeRemaining;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }
}

public class Main {
    // Used for the format of the date
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Method to find the waiting time for all processes
    static void findWaitingTime(ArrayList<Process> processes, int quantum) {

        // Ready Queue initialization
        Queue<Process> readyqueue = new LinkedList<>();
        // Initialize the clock
        Process.clock = 0;

        // Variable to track the context switch
        int context_switch = 0;

        //Incrementing Timer until the first process arrives
        while (Process.clock < processes.get(0).getArrivalTime()) {
            Process.clock++;
        }
        System.out.println("Log of Round Robin Events");
        // Add all the processes whose arrival times that are less than or equal to the clock to ready queue
        for (Process process : processes) {

            if (Process.clock >= process.getArrivalTime()) {
                System.out.println("Process " + process.getPid() +
                        " has entered into the ready queue at " + Process.clock + "ms");
                readyqueue.add(process);
                process.setInQueue(true); // Set their status to in ready queue
            }
        }


        // Keep traversing processes in round-robin manner
        // until all of them are done.
        while (true) {
            boolean done = true; // Variable used to track status of all processes

            // Traverse all processes one by one repeatedly
            while (!readyqueue.isEmpty()) { // Enter loop is ready queue is not empty
                // If burst time of a process is greater than need to process further
                if (readyqueue.peek().getBurstTimeRemaining() > 0) {
                    done = false; // There is a pending process

                    // Check if the current process burst time is more than the quantum
                    if (readyqueue.peek().getBurstTimeRemaining() > quantum) {

                        // Report that the Process is being Processed
                        System.out.println("Process " + readyqueue.peek().getPid()
                                + " has left ready queue and being processed at " + Process.clock + "ms");

                        if (readyqueue.peek().getResponseTime() == -1) {
                            readyqueue.peek().setResponseTime(Process.clock);
                            System.out.println("Process " + readyqueue.peek().getPid() +
                                    " has a response time of " + readyqueue.peek().getResponseTime() + "ms");
                        }
                        // Increase the clock (shows how much time a process has been processed)
                        Process.clock += quantum;

                        // Decrease the burst_time of current process by quantum
                        readyqueue.peek().setBurstTimeRemaining(readyqueue.peek().getBurstTimeRemaining() - quantum);

                        // check for more processes in the ready queue
                        for (Process process : processes) {

                            if ((Process.clock >= process.getArrivalTime()) && !process.isInQueue() && !process.isComplete()) {
                                System.out.println("Process " + process.getPid()
                                        + " has entered into the ready queue at " + Process.clock + "ms");
                                readyqueue.add(process);
                                process.setInQueue(true);
                            }
                        }
                        context_switch++;
                        System.out.println("Process " + readyqueue.peek().getPid()
                                + " has been placed back into the ready queue at " + Process.clock + "ms");
                        readyqueue.add(readyqueue.peek());
                        readyqueue.remove();

                    }

                    // If burst time is smaller than or equal to quantum. Last cycle for this process
                    else {
                        if (readyqueue.peek().getResponseTime() == -1) {
                            readyqueue.peek().setResponseTime(Process.clock);
                            System.out.println("Process " + readyqueue.peek().getPid() +
                                    " has a response time of " + readyqueue.peek().getResponseTime() + "ms");
                        }
                        // Increase the value of clock (show how much time a process has been processed)
                        Process.clock = Process.clock + readyqueue.peek().getBurstTimeRemaining();

                        // Waiting time is completion time - arrival time - burst time
                        readyqueue.peek().setWaitingTime(Process.clock - readyqueue.peek().getArrivalTime() -
                                readyqueue.peek().getBurstTime());

                        // As the process gets fully executed make its remaining burst time = 0
                        readyqueue.peek().setBurstTimeRemaining(0);

                        // Set the completion status to true
                        readyqueue.peek().setComplete(true);

                        // Set the status of the Process out of ready queue
                        readyqueue.peek().setInQueue(false);

                        // Set the completion time of the process
                        readyqueue.peek().setCompletionTime(Process.clock);
                        // Print the Process has left the ready queue
                        System.out.println("Process " + readyqueue.peek().getPid() +
                                " has left the ready queue at " + Process.clock + "ms");

                        // Print out at what time the process got completed
                        System.out.println("Process " + readyqueue.peek().getPid() +
                                " was completed at " + readyqueue.peek().getCompletionTime() + "ms");
                        // 2021-03-24 16:48:05


                        // Take the process out of the ready queue
                        readyqueue.remove();

                        context_switch++;
                    }
                }

                // check for more processes in the ready queue
                for (Process process : processes) {

                    if ((Process.clock >= process.getArrivalTime()) && !process.isInQueue() && !process.isComplete()) {
                        readyqueue.add(process);
                        process.setInQueue(true); // Set their status to in ready queue
                        System.out.println("Process " + process.getPid()
                                + " has entered into the ready queue at " + Process.clock + "ms");
                    }
                }
            }
            // Check to see if the CPU is idle
            for (Process process : processes) {
                if (!process.isComplete()) { // Check for the next process to enter the ready queue
                    readyqueue.add(process); // Add process to the ready queue
                    System.out.println("Process " + process.getPid() +
                            " has entered into the ready queue at " + Process.clock + "ms");
                    Process.clock += (process.getArrivalTime() - Process.clock); // Increase the clock
                    break;
                }
            }
            // If all processes are done and nothing in ready queue break loop
            if (done) {
                break;
            }

        }
        System.out.println("\nThere has been " + context_switch + " total context switches");
    }

    // Method to calculate turn around time
    static void findTurnAroundTime(ArrayList<Process> processes, int n) {
        // calculating turnaround time by adding Burst Time and Waiting Time
        for (int i = 0; i < n; i++) {
            processes.get(i).setTurnaroundTime(processes.get(i).getBurstTime() + processes.get(i).getWaitingTime());
        }
    }

    // Method to calculate average time
    static void roundRobin(ArrayList<Process> processes, int n, int quantum) {
        // Initializing variables of Total Wait Time and Total Turnaround Time
        int total_wt = 0, total_tat = 0;

        // Function to find waiting time of all processes
        findWaitingTime(processes, quantum);

        // Function to find turn around time for all processes
        findTurnAroundTime(processes, n);

        // After all processes are executed we resort the list by process ID
        processes.sort(new ProcessPIDComparator());

        // Display processes along with all details
        System.out.println("\nChart Details");
        System.out.println("Process ID" + "\t\t" + "Arrival Time" + "\t" + "Burst Time" + "\t" +
                "\t" + "Wait Time" + "\t" + "Turn Around Time");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < n; i++) {
            total_wt = total_wt + processes.get(i).getWaitingTime();
            total_tat = total_tat + processes.get(i).getTurnaroundTime();
            System.out.println(processes.get(i).getPid() + "\t\t\t\t" + processes.get(i).getArrivalTime()
                    + "\t\t\t\t" + processes.get(i).getBurstTime() + "\t\t\t\t" +
                    processes.get(i).getWaitingTime() + "\t\t\t" + processes.get(i).getTurnaroundTime());
        }
        // Calculate the CPU Utilization
        System.out.println("The CPU Utilization = " + (1 - ((float) Process.idleTime / (float) Process.clock)) * 100 + "%");

        // Calculate the Throughput
        System.out.println("The Throughput = " + ((float) processes.size() / (float) Process.clock)
                + " processes per unit of time");

        // Calculate the Average Waiting Time
        System.out.println("Average waiting time = " + ((float) total_wt / (float) n) + "ms");

        // Calculate the Average Turn Around Time
        System.out.println("Average turn around time = " + ((float) total_tat / (float) n) + "ms");

    }

    // Static object for the timestamp
    static Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    static Scanner inp = new Scanner(System.in); // Creation of Scanner Object

    //Driver Method
    public static void main(String[] args) {

        String filename;
        ArrayList<Process> processes = new ArrayList<>(); // List data structure to hold all the processes
        do {
            String line;
            String splitBy = ",";
            System.out.print("Enter the name of the file : ");
            filename = inp.nextLine();
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                System.out.println("\n");
                while ((line = br.readLine()) != null) {
                    String[] process = line.split(splitBy);
                    Process new_process = new Process(); // Creation of a Process
                    try {

                        // Setting a process id
                        new_process.setPid(Integer.parseInt(process[0]));
                        //  Setting Arrival Time
                        new_process.setArrivalTime(Integer.parseInt(process[1]));
                        // Setting Burst Time
                        new_process.setBurstTime(Integer.parseInt(process[2]));
                        // Setting Remaining Burst Time
                        new_process.setBurstTimeRemaining(Integer.parseInt(process[2]));

                        //Printing out the timestamp of when the Process was created
                        System.out.println("Process ID " + new_process.getPid() +
                                " created at " + sdf3.format(timestamp));         // 2021-03-24 16:48:05
                        processes.add(new_process); // Add process to the list of processes

                    } catch (NumberFormatException ignored) { //Catch header of file "pid, arrive, burst" (Ignore it)
                    }
                }
                break;
            } catch (FileNotFoundException e) { //Catch if the filename is not found
                System.out.println("Please enter the correct filename");
            } catch (IOException e) { // Catch any other IO Exception
                e.printStackTrace();
            }
        } while (true); // Do it until the user enters the correct file with correct information
        while (true) {
            int quantum = 0; // initialize the variable for quantum
            do {
                try {
                    System.out.print("Enter the time quantum : ");
                    quantum = inp.nextInt();
                    inp.nextLine();
                /* Check to see if the quantum value is more than 0.
                If it is less than 1 than throw an exception and tell the user to enter a number more than 0
                Repeat this until the user enters a valid number
                * */
                    if (quantum < 1) {
                        throw new InputMismatchException();
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number more than 0");
                }
            } while (quantum < 1);

        /*  This is important to the algorithm. Sorting the list of processes by Arrival time.
            Also, if the Arrival Times are the same of processes we will sort by Process IDs.
        * */
            processes.sort(new ProcessArrivalComparator());
            for (Process process : processes) {
                process.setResponseTime(-1);
                process.setInQueue(false);
                process.setComplete(false);
                process.setCompletionTime(0);
                process.setBurstTimeRemaining(process.getBurstTime());
                process.setTurnaroundTime(0);
                process.setWaitingTime(0);
            }
            System.out.println("*****************************************************************************");
            System.out.println("\n");

            // Call the Round Robin Function
            roundRobin(processes, processes.size(), quantum);
            String answer = "";
            do {
                System.out.println("Type to Quit (Q) or to Simulate again (S) ");
                answer = inp.nextLine().toUpperCase();

            } while (!answer.equals("S") && !answer.equals("Q"));

            if (answer.equals("Q")) {
                System.out.println("Thank you for using my Round Robin simulator :)");
                break;
            } else {
                System.out.println();
            }

        }


    }
}
