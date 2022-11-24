package process.mapping;


import errors.MyError;

public class ReadBed {
    public String chr;
    public long start;
    public long end;
    public String readName;
    public int mapq;
    public char strand;
    public String cigar;
    public int readLen;
    public int rS;
    public int rE;

    public ReadBed(String[] bedLine) {
        if (bedLine.length<7){
            MyError.BedError(bedLine.length);
        }
        this.chr = bedLine[0];
        this.start = Integer.parseInt(bedLine[1]);
        this.end = Integer.parseInt(bedLine[2]);
        this.readName = bedLine[3];
        this.mapq = Integer.parseInt(bedLine[4]);
        this.strand = bedLine[5].charAt(0);
        this.cigar = bedLine[6];
        this.readLen = getReadlen();
        this.rS = getReadStart();
        this.rE = getReadEnd();

    }

    public ReadBed(String chr, long start, long end, String readName, int mapq, char strand, String cigar) {
        this.chr = chr;
        this.start = start;
        this.end = end;
        this.readName = readName;
        this.mapq = mapq;
        this.strand = strand;
        this.cigar = cigar;
    }

    private int getReadlen() {
        if(cigar==null) return 0;
        String readseq = "";
        int readlen = 0;
        for(int i=0;i<cigar.length();i++) {
            if(cigar.charAt(i) >=48 && cigar.charAt(i) <=57) {
                readseq += cigar.charAt(i);
            }else if(cigar.charAt(i) == 'M') {
                readlen += Integer.parseInt(readseq);
                readseq = "";
            }else if(cigar.charAt(i) == 'I') {
                readlen += Integer.parseInt(readseq);
                readseq = "";
            }else if(cigar.charAt(i) == 'H') {
                readlen += Integer.parseInt(readseq);
                readseq = "";
            }else if(cigar.charAt(i) == 'S') {
                readlen += Integer.parseInt(readseq);
                readseq = "";
            }else {
                readseq = "";
            }
        }
        return readlen;
    }

    private int getReadStart() {
        if(cigar==null) return -1;
        String readseq = "";
        int readlen = 1;
        for(int i=0;i<cigar.length();i++) {
            if(cigar.charAt(i) >=48 && cigar.charAt(i) <=57) {
                readseq += cigar.charAt(i);
            }else if(cigar.charAt(i) == 'M') {
                return readlen;
            }else if(cigar.charAt(i) == 'I') {
                return readlen;
            }else if(cigar.charAt(i) == 'H') {
                readlen += Integer.parseInt(readseq);
                return readlen;
            }else if(cigar.charAt(i) == 'S') {
                readlen += Integer.parseInt(readseq);
                return readlen;
            }
        }
        return -1;
    }

    private int getReadEnd() {
        if(cigar==null) return -1;
        if (cigar.endsWith("H") || cigar.endsWith("S")){
            String readseq = "";
            for(int i=cigar.length()-2;i>=0;i--) {
                if(cigar.charAt(i) >=48 && cigar.charAt(i) <=57) {
                    readseq = cigar.charAt(i) + readseq;
                }else if(cigar.charAt(i) == 'M') {
                    break;
                }else if(cigar.charAt(i) == 'I') {
                    break;
                }else if(cigar.charAt(i) == 'H') {
                    break;
                }else if(cigar.charAt(i) == 'S') {
                    break;
                }
            }
            return readLen - Integer.parseInt(readseq);
        }else {
            return readLen;
        }
    }

    public String toAlignTable(){
        String atLine = chr + '\t'
        + start + '\t'
        + end + '\t'
        + readName + '\t'
        + rS + '\t'
        + rE + '\t'
        + readLen + '\t'
        + mapq + '\t'
        + strand;
        return atLine;
    }
}
