import java.util.Comparator;

public class ProcessArrivalComparator implements Comparator<Process> {

    @Override
    public int compare(Process o1, Process o2) {
        if(o1.getArrivalTime() == o2.getArrivalTime()){
            return o1.getPid() - o2.getPid();
        }
        else{
            return o1.getArrivalTime() - o2.getArrivalTime();
        }
    }
}
