import java.io.*;
import java.io.BufferedReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


class Process {
    private int pid;
    private int arrivalTime;
    private int burstTime;
    private int burstTimeRemaining; // the amount of CPU time remaining after each execution
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private boolean isComplete;
    private boolean inQueue;

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

    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Method to find the waiting time for all
    // processes
    static void findWaitingTime(ArrayList<Process> processes, int quantum) {

        Queue<Process> readyqueue = new LinkedList<>();
        int timer = 0;

        while (timer < processes.get(0).getArrivalTime()) {    //Incrementing Timer until the first process arrives
            timer++;
        }

        for (Process process : processes) {

            if (timer >= process.getArrivalTime()) {
                readyqueue.add(process);
                process.setInQueue(true);
            }
        }


        // Keep traversing processes in round-robin manner
        // until all of them are not done.
        while (true) {
            boolean done = true;

            // Traverse all processes one by one repeatedly
            while (!readyqueue.isEmpty()) {
                // If burst time of a process is greater than 0
                // then only need to process further
                if (readyqueue.peek().getBurstTimeRemaining() > 0) {
                    done = false; // There is a pending process

                    if (readyqueue.peek().getBurstTimeRemaining() > quantum) {
                        // Increase the value of t i.e. shows
                        // how much time a process has been processed
                        timer += quantum;

                        // Decrease the burst_time of current process
                        // by quantum
                        //rem_bt.set(i, quantum);
                        readyqueue.peek().setBurstTimeRemaining(readyqueue.peek().getBurstTimeRemaining() - quantum);
                        // check for more in the ready queue
                        for (Process process : processes) {

                            if ((timer >= process.getArrivalTime()) && !process.isInQueue() && !process.isComplete()) {
                                readyqueue.add(process);
                                process.setInQueue(true);
                            }
                        }
                        readyqueue.add(readyqueue.peek());
                        readyqueue.remove();
                    }

                    // If burst time is smaller than or equal to
                    // quantum. Last cycle for this process
                    else {
                        // Increase the value of t i.e. shows
                        // how much time a process has been processed
                        timer = timer + readyqueue.peek().getBurstTimeRemaining();

                        // Waiting time is current time minus time
                        // used by this process
                        readyqueue.peek().setWaitingTime(timer - readyqueue.peek().getArrivalTime() - readyqueue.peek().getBurstTime()); // turn around time - burst time = completion time - arrival time - burst time

                        // As the process gets fully executed
                        // make its remaining burst time = 0
                        //rem_bt.set(i, 0);
                        readyqueue.peek().setBurstTimeRemaining(0);
                        readyqueue.peek().setComplete(true);
                        readyqueue.peek().setInQueue(false);
                        System.out.println("Process ID " + readyqueue.peek().getPid() + " was completed at " + timer + "ms");         // 2021-03-24 16:48:05
                        readyqueue.remove();
                    }
                }
                for (Process process : processes) {

                    if ((timer >= process.getArrivalTime()) && !process.isInQueue() && !process.isComplete()) {
                        readyqueue.add(process);
                        process.setInQueue(true);
                    }
                }
            }
            // check to see if the CPU is idle
            for (Process process : processes) {
                if (!process.isComplete()) {
                    readyqueue.add(process);
                    timer += (process.getArrivalTime() - timer);
                    break;
                }
            }
            // If all processes are done and nothing in ready queue break loop
            if (done)
                break;
        }
    }

    // Method to calculate turn around time
    static void findTurnAroundTime(ArrayList<Process> processes, int n) {
        // calculating turnaround time by adding
        // bt[i] + wt[i]
        for (int i = 0; i < n; i++) {
            processes.get(i).setTurnaroundTime(processes.get(i).getBurstTime() + processes.get(i).getWaitingTime());
        }
    }

    // Method to calculate average time
    static void findavgTime(ArrayList<Process> processes, int n, int quantum) {
//        int[] wt = new int[n], tat = new int[n];
        int total_wt = 0, total_tat = 0;

        // Function to find waiting time of all processes
        findWaitingTime(processes, quantum);



        // Function to find turn around time for all processes
        findTurnAroundTime(processes, n);

        processes.sort(new ProcessPIDComparator());

        // Display processes along with all details
        System.out.println("Process ID" + "\t\t" + "Arrival Time" + "\t" + "Burst Time" + "\t" +
                "\t" + "Wait Time" + "\t" + "Turn Around Time");

        // Calculate total waiting time and total turn
        // around time
        for (int i = 0; i < n; i++) {
            total_wt = total_wt + processes.get(i).getWaitingTime();
            total_tat = total_tat + processes.get(i).getTurnaroundTime();
            System.out.println(processes.get(i).getPid() + "\t\t\t\t" + processes.get(i).getArrivalTime() + "\t\t\t\t" + processes.get(i).getBurstTime() + "\t\t\t\t" +
                    processes.get(i).getWaitingTime() + "\t\t\t" + processes.get(i).getTurnaroundTime());
        }

        System.out.println("Average waiting time = " +
                (float) total_wt / (float) n);
        System.out.println("Average turn around time = " +
                (float) total_tat / (float) n);
    }

    static Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    //Driver Method
    public static void main(String[] args) {
        Scanner inp = new Scanner(System.in);

        String filename;
        ArrayList<Process> processes = new ArrayList<>();
        do {
            String line;
            String splitBy = ",";
            System.out.print("Enter the name of the file : ");
            filename = inp.nextLine();
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                while ((line = br.readLine()) != null) {
                    String[] process = line.split(splitBy);
                    Process new_process = new Process();
                    try {

                        new_process.setPid(Integer.parseInt(process[0]));
                        new_process.setArrivalTime(Integer.parseInt(process[1]));
                        new_process.setBurstTime(Integer.parseInt(process[2]));
                        new_process.setBurstTimeRemaining(Integer.parseInt(process[2]));
                        System.out.println("Process ID " + new_process.getPid() + " created at " + sdf3.format(timestamp));         // 2021-03-24 16:48:05
                        processes.add(new_process);
                    } catch (NumberFormatException ignored) {
                    }
                }
                break;
            } catch (FileNotFoundException e) {
                System.out.println("Please enter the correct filename");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);

        int quantum = 0;
        do {
            try {
                System.out.print("Enter the time quantum : ");
                quantum = inp.nextInt();
                if (quantum < 1) {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number more than 0");
            }
        } while (quantum < 1);

        processes.sort(new ProcessArrivalComparator());

        findavgTime(processes, processes.size(), quantum);

    }
}
