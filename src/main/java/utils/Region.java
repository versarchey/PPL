package utils;

public class Region {
    String chr;
    long start;
    long end;

    public Region(String chr, long start, long end) {
        this.chr = chr;
        this.start = start;
        this.end = end;
    }

    public Region(String chr, String start, String end) {
        this.chr = chr;
        this.start = Long.parseLong(start);
        this.end = Long.parseLong(end);
    }

    public String toString(){
        return this.chr+"\t"+this.start+"\t"+this.end;
    }
}
