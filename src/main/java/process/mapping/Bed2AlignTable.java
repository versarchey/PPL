package process.mapping;

import java.io.*;
import java.util.Arrays;

public class Bed2AlignTable {
    public Bed2AlignTable(String inputfile, String outputFile) throws IOException {
        BufferedReader bedReader = new BufferedReader( new FileReader(inputfile));
        new File(outputFile).delete();
        BufferedWriter atWriter = new BufferedWriter( new FileWriter(outputFile));

        String line;
        long num=0;
        while ((line = bedReader.readLine())!=null){
            String[] fields = line.split("\t");
            //filter mapq < cutoff reads
//            if (Integer.parseInt(fields[4]) < cutoff) continue;
            ReadBed one = new ReadBed(fields);
            atWriter.write( one.toAlignTable()+"\t"+num);
            num++;
            atWriter.newLine();
        }
        bedReader.close();
        atWriter.close();
    }


    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            new Bed2AlignTable(args[0], args[1]+".aligntable");
        } else {
            System.out.println("Usage: java -cp *.jar proBam2AlignTable <inputFile> <OutputFile>");
        }

    }
}
