package utils;

import org.jetbrains.annotations.NotNull;

public class Interaction implements Comparable<Interaction>{
    public long binId1;
    public long binId2;
    public long count;
    public double countIced;

    public Interaction() {
    }

    public Interaction(long binId1, long binId2) {
        this.binId1 = binId1;
        this.binId2 = binId2;
        count=0;
        countIced=0;
    }

    public Interaction(long binId1, long binId2, long count, double countIced) {
        this.binId1 = binId1;
        this.binId2 = binId2;
        this.count = count;
        this.countIced = countIced;
    }

    public void normalizedCount(){
        countIced=count;
    }

    @Override
    public int compareTo(@NotNull Interaction o) {
        if (this.binId1 > o.binId1 ||
                this.binId1 == o.binId1 && this.binId2 > o.binId2){
            return 1;
        }else if (this.binId1 == o.binId1 && this.binId2 == o.binId2 ){
            return 0;
        }else{
            return -1;
        }
    }

    @Override
    public String toString() {
        return binId1+"\t"+binId2+"\t"+count;
    }

    public String toStringIced() {
        return binId1+"\t"+binId2+"\t"+countIced;
    }
}
