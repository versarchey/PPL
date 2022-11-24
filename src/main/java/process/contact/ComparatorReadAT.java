package process.contact;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ComparatorReadAT implements Comparator<ReadAT> {

    @Override
    public int compare(ReadAT o1, ReadAT o2) {
        if (o1.index > o2.index){
            return 1;
        }else if (o1.index < o2.index){
            return -2;
        }else {
            return 0;
        }
    }
}
