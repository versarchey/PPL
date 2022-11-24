package utils;

import java.util.Comparator;

public class InteractionComparator implements Comparator<Interaction> {
    @Override
    public int compare(Interaction o1, Interaction o2) {
        if (o1.binId1 > o2.binId1 ||
                o1.binId1 == o2.binId1 && o1.binId2 > o2.binId2){
            return 1;
        }else if (o1.binId1 == o2.binId1 && o1.binId2 == o2.binId2 ){
            return 0;
        }else{
            return -1;
        }
    }
}
