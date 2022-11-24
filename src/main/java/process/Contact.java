package process;

import process.contact.AlignTable2Contact;

import java.io.IOException;
import java.util.Calendar;

public class Contact {
    private Path p;
    private String outPrefix;
    private Calendar rightNow = Calendar.getInstance();

    public Contact(Path path){
        p = path;
        outPrefix = p.OUTPUT_DIRECTORY+"/"+p.OUTPUT_PREFIX+"/"+p.OUTPUT_PREFIX;
    }

    public void extratContact() throws IOException {
        AlignTable2Contact.run(p,outPrefix);
    }
}
