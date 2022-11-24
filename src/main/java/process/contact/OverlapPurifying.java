package process.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverlapPurifying {

    List<ReadAT> concatemerPurified;
    List<ReadAT> concatemerPurifiedRemove;

    public OverlapPurifying() {
        concatemerPurified=new ArrayList<>();
        concatemerPurifiedRemove = new ArrayList<>();
    }

    public List<ReadAT> purify(List<ReadAT> concatemer, int len){
        Map<String,ArrayList<ReadAT>> tmpMap = new HashMap<>();
        for (ReadAT at :
                concatemer) {
            if(tmpMap.containsKey(at.readNameSplit)){
                tmpMap.get(at.readNameSplit).add(at);
            }else{
                ArrayList<ReadAT> tmpList = new ArrayList<ReadAT>();
                tmpList.add(at);
                tmpMap.put(at.readNameSplit, tmpList);
            }
        }
        for (Map.Entry<String,ArrayList<ReadAT>> entry:
             tmpMap.entrySet()) {
            if (entry.getValue().size() == 1){
                concatemerPurified.addAll(entry.getValue());
            }else {
                GraphConcatemer graphConcatemer = new GraphConcatemer(entry.getValue());
                graphConcatemer.IdentifyMonomersFromConcatemer();
                concatemerPurified.addAll(graphConcatemer.monomers);
                entry.getValue().removeAll(graphConcatemer.monomers);
                concatemerPurifiedRemove.addAll(entry.getValue());
            }
        }
        concatemer.clear();
        concatemer.addAll(concatemerPurified);
        return concatemerPurifiedRemove;
    }

    public void clear(){
        concatemerPurified.clear();
        concatemerPurifiedRemove.clear();
    }
}
