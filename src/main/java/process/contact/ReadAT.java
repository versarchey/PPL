package process.contact;


import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ReadAT implements Comparable<ReadAT>{
    public String chr;
    public long start;
    public long end;
    public String readName;
    public String readNameSplit;
    public int rS;
    public int rE;
    public int readLen;
    public int mapq;
    public char strand;
    public long index=-1;

    public ReadAT() {
    }

    public ReadAT(String[] atLine) {
        this.chr = atLine[0];
        this.start = Integer.parseInt(atLine[1]);
        this.end = Integer.parseInt(atLine[2]);
        this.readNameSplit = atLine[3];
        this.readName = StringUtils.substringBeforeLast(atLine[3], "_");
        this.rS = Integer.parseInt(atLine[4]);
        this.rE = Integer.parseInt(atLine[5]);
        this.readLen = Integer.parseInt(atLine[6]);
        this.mapq = Integer.parseInt(atLine[7]);
        this.strand = atLine[8].charAt(0);
        if (atLine.length==10){
            this.index=Long.parseLong(atLine[9]);
        }

    }

    public ReadAT(String chr, long start, long end, String readName, int rS, int rE, int readLen, int mapq, char strand) {
        this.chr = chr;
        this.start = start;
        this.end = end;
        this.readName = readName;
        this.rS = rS;
        this.rE = rE;
        this.readLen = readLen;
        this.mapq = mapq;
        this.strand = strand;
    }

    public String toContactLine(){
        String atLine = chr + '\t'
        + start + '\t'
        + end + '\t'
        + readName + '\t'
        + rS + '\t'
        + rE + '\t'
        + readLen + '\t'
        + mapq + '\t'
        + strand + '\t'
        + index;
        return atLine;
    }

    @Override
    public int compareTo(@NotNull ReadAT o) {
        if (this.index > o.index){
            return 1;
        }else if (this.index < o.index){
            return -2;
        }else {
            return 0;
        }
    }
}
