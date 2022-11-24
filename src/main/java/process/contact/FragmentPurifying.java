package process.contact;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.google.common.collect.*;
import jdk.nashorn.tools.Shell;
import process.contact.ReadAT;

public class FragmentPurifying {

    String restrictionsiteFile;
    RangeMap<Long, Long> rangeMap;
    List<ReadAT> concatemerPurified;
    List<ReadAT> concatemerPurifiedRemove;
    int minfragsize;
    int maxfragsize;

    public FragmentPurifying(String restrictionFile, int minfragsize, int maxfragsize) throws IOException {
        restrictionsiteFile=restrictionsiteFile;
        concatemerPurified = new ArrayList<ReadAT>();
        concatemerPurifiedRemove = new ArrayList<ReadAT>();
        this.minfragsize=minfragsize;
        this.maxfragsize=maxfragsize;
        this.rangeMap = TreeRangeMap.create();
        BufferedReader reader = new BufferedReader(new FileReader(new File(restrictionFile)));
        String line;
        long id = 0;

        while ((line=reader.readLine())!=null){
            String[] fields = line.split("\t");
            rangeMap.put(Range.openClosed(Long.parseLong(fields[1]), Long.parseLong(fields[2])),id);
            id++;
        }
        reader.close();
    }


    public int removePETinsameblock(List<ReadAT> concatemer) throws IOException {
        return 0;
    }

    public List<ReadAT> purify(List<ReadAT> concatemer) throws IOException {
        String line="";
        long num1=0;
        long num2=0;
        long num3=0;
        HashMap<Long, ReadAT> tmpMap = new HashMap<Long, ReadAT>();

        for (ReadAT at :
                concatemer) {
            Range<Long> range = Range.open(at.start, at.end);
//            Range<Long> range1 = rangeMap.getEntry(at.start).getKey();
//            Range<Long> range2 = rangeMap.getEntry(at.end).getKey();
//            Long left = range1.lowerEndpoint();
//            Long lr = range1.upperEndpoint();
//            Long rl = range2.lowerEndpoint();
//            Long right = range2.upperEndpoint();
//            if (((at.start-left)<30||(lr- at.start)<30)&&((right-at.end)<30||(at.end-rl)<30)){
//                num1++;
//            }else{
//                num2++;
//            }
//            if (((at.start-left)<50||(lr- at.start)<50)&&((right-at.end)<50||(at.end-rl)<50)){
//                num3++;
//            }
            //当两个比对位于的消化片段一致时，去除mapq较低的一个
            Long id = this.rangeMap.get((at.start+at.end)/2);
            if (tmpMap.containsKey(id)){
                if (tmpMap.get(id).mapq<at.mapq){
                    concatemerPurifiedRemove.add(tmpMap.remove(id));
                    tmpMap.put(id,at);
                }else {
                    concatemerPurifiedRemove.add(at);
                }
            }else{
                tmpMap.put(id,at);
            }
        }
        for (Map.Entry<Long,ReadAT> entry :
                 tmpMap.entrySet()) {
            concatemerPurified.add(entry.getValue());
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
