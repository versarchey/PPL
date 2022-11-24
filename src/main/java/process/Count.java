package process;

import java.io.*;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

import static utils.MyUtil.isGZipped;

public class Count {
    public static long countReads(Path p) throws IOException {
        long nPETs_hichip = 0;
        Calendar rightNow = Calendar.getInstance();
        ///使用数组存储输入的一个或多个fastq文件
        String[] fastqs = p.Fastq_file.split(",");
        for(int jk = 0; jk < fastqs.length; jk++) {
            BufferedReader fastqFileIn;
            File fastq = new File(fastqs[jk]);
            //针对是否为压缩文件，使用不同的输入流来读取文件
            if(isGZipped(fastq)) {
                System.out.println("[" + rightNow.getTime().toString() +"] Counting total reads with gzip fastq file, " + fastqs[jk]);
                fastqFileIn  = new BufferedReader(
                        new InputStreamReader(new GZIPInputStream(new FileInputStream(fastq))));
            }else {
                System.out.println("[" + rightNow.getTime().toString() +"] Counting total reads with fastq file, " + fastqs[jk]);
                fastqFileIn = new BufferedReader(new FileReader(fastq));
            }

            String readline = fastqFileIn.readLine();

            while (readline != null) {
                nPETs_hichip++;
                readline = fastqFileIn.readLine();
            }
            fastqFileIn.close();
        }
        return nPETs_hichip;
    }
}
