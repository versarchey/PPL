package process.contact;

import process.contact.mygraph.Edge;
import process.contact.mygraph.IDirectGraph;
import process.contact.mygraph.ListDirectGraph;
import process.mapping.ReadBed;

import java.util.*;

public class GraphConcatemer {
//    Map<ReadAT, Long[]> monomers;
    List<ReadAT> monomers = new ArrayList<ReadAT>();
    public GraphConcatemer(List<ReadAT> monomers){
        this.monomers.addAll(monomers);
    }

    public void IdentifyMonomersFromConcatemer(){
        IDirectGraph<ReadAT> graph = new ListDirectGraph<ReadAT>();
        List<ReadAT> newMonomers = new ArrayList<>();

        //图的建立：添加点
        ReadAT START = new ReadAT();
        START.rE=1;
        ReadAT END = new ReadAT();
        END.rE=monomers.get(0).readLen;
        monomers.add(START);
        monomers.add(END);
        for (ReadAT monomer :
                monomers) {
            graph.addVertex(monomer);
        }

        //图的建立：添加边
        for (ReadAT monomer1 :
                monomers) {
            for (ReadAT monomer2:
                 monomers) {
                if (monomer1 == monomer2) continue;
                if (monomer1 == END) continue;
                if (monomer2 == START) continue;
                if (monomer1 == START && monomer2 == END) continue;
                if (monomer1 == START){
                    double weight=monomer2.rS-monomer2.mapq;
                    graph.addEdge(new Edge<>(monomer1,monomer2,weight));
                    continue;
                }
                if (monomer2 == END){
                    double weight=monomer2.rS-monomer1.rE;
                    graph.addEdge(new Edge<>(monomer1,monomer2,weight));
                    continue;
                }
                if (monomer1.rE<monomer2.rE || (monomer1.rE==monomer2.rE && monomer1.rS < monomer2.rS)){
                    double weight=Math.abs(monomer1.rE-monomer2.rS)-monomer2.mapq;
                    graph.addEdge(new Edge<>(monomer1,monomer2,weight));
                }
            }
        }
        newMonomers=graph.BellmanFord(START, END);
        newMonomers.remove(0);
        newMonomers.remove(newMonomers.size()-1);
        monomers = newMonomers;
    }
    
}
