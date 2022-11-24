package process.contact;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import java.util.*;

public class DistancePurifying {
    List<ReadAT> concatemerPurified;
    List<ReadAT> concatemerPurifiedRemove;

    public DistancePurifying() {
        concatemerPurified=new ArrayList<>();
        concatemerPurifiedRemove = new ArrayList<>();
    }

    public List<ReadAT> purify(List<ReadAT> concatemer, long dis) {
        Map<Range<Long>, ReadAT> rangeMap;
        Map<Range<Long>, ReadAT> tmpMap;
        rangeMap = new HashMap<>();
        tmpMap = new HashMap<>();


        for (ReadAT mm :
                concatemer) {

            if (rangeMap.isEmpty()) {
                rangeMap.put(Range.closed(mm.start, mm.end),mm);
            }else {
                tmpMap.clear();
                tmpMap.putAll(rangeMap);
                for (Map.Entry<Range<Long>, ReadAT> entry:
                        rangeMap.entrySet()){
                    if (entry.getKey().isConnected(Range.closed(mm.start-dis, mm.end+dis))){
                        if (entry.getValue().mapq< mm.mapq){
                            concatemerPurifiedRemove.add(tmpMap.remove(entry.getKey()));
                            tmpMap.put(Range.closed(mm.start, mm.end),mm);
                        }else{
                            if (tmpMap.containsKey(Range.closed(mm.start, mm.end)))
                                concatemerPurifiedRemove.add(tmpMap.remove(Range.closed(mm.start, mm.end)));
                            else
                                concatemerPurifiedRemove.add(mm);
                            break;
                        }
                    }else {
                        tmpMap.put(Range.closed(mm.start, mm.end),mm);
                    }
                }
                rangeMap.clear();
                rangeMap.putAll(tmpMap);
            }


        }
        for (Map.Entry<Range<Long>, ReadAT> entry :
                rangeMap.entrySet()) {
            concatemerPurified.add(entry.getValue());
        }
        concatemer.clear();
        concatemer.addAll(concatemerPurified);
        return concatemerPurifiedRemove;
    }

    public void clean(){
        concatemerPurified.clear();
        concatemerPurifiedRemove.clear();
    }
}
