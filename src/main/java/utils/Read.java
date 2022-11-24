package utils;

public class Read {
    String name;
    String seq;
    String quality;
    String added;

    public Read(String name, String seq, String quality, String added) {
        this.name = name;
        this.seq = seq;
        this.quality = quality;
        this.added = added;
    }

    public Read() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }
}
