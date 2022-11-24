package utils;

import process.mapping.Bed2AlignTable;

import java.io.IOException;

public class Bed2Contact {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java -cp pore-c_tool.jar utils.Bed2Contact <.cigar_bed> <aligntable>");
            System.exit(0);
        }
        new Bed2AlignTable(args[0], args[1]);
    }
}
