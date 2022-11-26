import java.util.Comparator;

public class ProcessPIDComparator implements Comparator<Process> {

    @Override
    public int compare(Process o1, Process o2) {
        return o1.getPid() - o2.getPid();
    }
}
