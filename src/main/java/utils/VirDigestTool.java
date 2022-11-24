package utils;

import process.Path;
import process.VirDigest;

import java.io.IOException;

public class VirDigestTool {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java -cp pore-c_tool.jar utils.VirDigestTool <genomeFile> <enzyme/resSite> <result.bed>");
            System.exit(0);
        }

        VirDigest vd = new VirDigest();
        Path path = new Path(args[0], args[1], args[2]);
        vd.processVD(path);



    }
}
