package process;

import utils.MyUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static utils.MyUtil.*;

public class QC {
    private static String[] cleanFastqWithFastp(String fastq1, String fastq2, String Nthread, String outdir, String prefix) throws IOException {
        String outPrefix = outdir + "/" + prefix + "/" + prefix;
        String line = "";
        BufferedWriter cleanfqF = new BufferedWriter(new FileWriter(outPrefix +
                ".cleanfastp.sh", false));

        String newfq1="", newfq2="";
        String[] fastq1s = fastq1.split(",");
        if(!fastq2.equals("")) {
            String[] fastq2s = fastq2.split(",");
            if(fastq1s.length!=fastq2s.length) {
                System.out.println("Error: fastq1 and fastq2 with different number of files!!!");
                System.exit(0);
            }
            for(int i=0;i<fastq1s.length;i++) {
                line="fastp -M 10 -Q -w "+ Nthread + " -i " + fastq1s[i] + " -o " + outPrefix+".clean." +i+"_R1.fq.gz " + " -I " + fastq2s[i] + " -O "+outPrefix+".clean."+
                        i+"_R2.fq.gz";
                cleanfqF.write(line);
                cleanfqF.newLine();
                if(i==0) {
                    newfq1=outPrefix+".clean." +i+"_R1.fq.gz";
                    newfq2=outPrefix+".clean." +i+"_R2.fq.gz";
                }else {
                    newfq1=newfq1+","+outPrefix+".clean." +i+"_R1.fq.gz";
                    newfq2=newfq2+","+outPrefix+".clean." +i+"_R2.fq.gz";
                }
            }
        }else {
            for(int i=0;i<fastq1s.length;i++) {
                line="fastp -M 10 -Q -w "+ Nthread + " -i " + fastq1s[i] + " -o "+outPrefix+".clean." +i+"_R1.fq.gz ";
                cleanfqF.write(line);
                cleanfqF.newLine();
                if(i==0) {
                    newfq1=outPrefix+".clean." +i+"_R1.fq.gz";
                }else {
                    newfq1=newfq1+","+outPrefix+".clean." +i+"_R1.fq.gz";
                }
            }
        }

        cleanfqF.close();
        MyUtil.runShell(outPrefix + ".cleanfastp.sh");
        new File(outPrefix + ".cleanfastp.sh").delete();
        String[] retString = new String[] {newfq1, newfq2};
        return retString;
    }

    public void processQC(Path p) throws IOException {
            if(!p.fastp.equals("fastp")) {
                checkPath(p.fastp);
            }
            String[] newfqs = cleanFastqWithFastp(p.Fastq_file_1, p.Fastq_file_2, p.NTHREADS, p.OUTPUT_DIRECTORY, p.OUTPUT_PREFIX);
            p.Fastq_file_1 = newfqs[0];
            p.Fastq_file_2 = newfqs[1];
        //System.out.println("TTT " + p.Fastq_file_1 + "  " + p.Fastq_file_2);
    }
}
