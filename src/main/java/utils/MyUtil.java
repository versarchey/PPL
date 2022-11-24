package utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static errors.MyError.*;

public class MyUtil {

    private static final int BUFFER_SIZE = 1024;


    public static void readGZip(File file) {

        GZIPInputStream gzipInputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            gzipInputStream = new GZIPInputStream(new FileInputStream(file));

            baos = new ByteArrayOutputStream();

            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;
            while((len=gzipInputStream.read(buf, 0, BUFFER_SIZE))!=-1){
                baos.write(buf, 0, len);
            }

            baos.toByteArray();

            String result = baos.toString("UTF-8");

            System.out.println("result="+result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(gzipInputStream!=null){
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeGZip(File inputFile, File outputFile) {

        GZIPOutputStream gzipOutputStream = null;
        InputStream in = null;
        try {
            gzipOutputStream = new GZIPOutputStream(new FileOutputStream(outputFile));

            in = new FileInputStream(inputFile);

            byte[] buffer = new byte[BUFFER_SIZE];

            int len = 0;
            while ((len = in.read(buffer,0,BUFFER_SIZE)) > 0) {
                gzipOutputStream.write(buffer, 0, len);
            }
            gzipOutputStream.finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(gzipOutputStream!=null){
                try {
                    gzipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void checkNY(String str) {
        if (!(str.equals("Y") || str.equals("N"))){
            inputNotNYError(str);
        }
    }

    public static void checkDigit(String str) {
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                inputNotNumError(str);
            }
        }
    }

    public static void checkPath(String str) {
        File f = new File(str);
        if (!f.exists()) {
            fileNotFoundError(str);
        }
    }
    //检查路径，但不退出
    public static boolean checkPathV(String str) {
        File f = new File(str);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    //执行shell文件
    public static void runShell(String path) {
        File f = new File(path);
        if (f.exists()) {
            try {
                String reBash = "bash";
                String command = "sh " + path;// notice the blank behind sh
                System.out.println("[Pore-C run] " + command);
                Process process0 = Runtime.getRuntime().exec(reBash);
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String message = null;
                while ((message = out.readLine()) != null) {
                    System.out.println(message);
                }
                while ((message = error.readLine()) != null) {
                    System.out.println(message);
                }
                process.waitFor();
                out.close();
                error.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: "+path+" doesn't exist");
        }
    }

    //执行shell命令并返回打印结果
    public static void shellrun(String command, String runcase) {
        Process process = null;

        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()
                    )
            );
            String data = "";
            while((data = reader.readLine()) != null) {
                System.out.println(data);
            }

            int exitValue = process.waitFor();

            if(exitValue != 0 && !runcase.equals("rm")) {
                System.out.println("error");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGZipped(File f) {
        int magic = 0;
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
            raf.close();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }

    public static void writeFile(String f, String s, boolean append) {
        File file = new File(f);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, append);
            fw.write(s+"\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int findstartinres(String res) {
        int i = -1;
        for(i=0; i<res.length(); i++) {
            if(res.charAt(i)=='^') {
                return i;
            }
        }
        return i;
    }

    public static int[] findstartinres_arry(String[] resS) {
        int[] sites = new int[resS.length];
        for(int i=0; i<resS.length; i++) {
            sites[i] = findstartinres(resS[i]);
            if(sites[i] == -1) {
                System.out.println("Error: can not find '^' in ligation site!!!!\n");
                System.exit(0);
            }
        }
        return sites;
    }

    public static int[] findsmIndex(StringBuilder chrseqbuilder, String[] resS, int start, int[] resloci, int chrlen) {
        int newindex = chrlen+1, residx = -1;

        for(int i=0;i<resS.length; i++) {
            if(resloci[i]<=start) {
                resloci[i] = chrseqbuilder.indexOf(resS[i], start);
            }
        }

        for(int i=0; i<resloci.length; i++) {
            if(resloci[i]>=0 && resloci[i] < newindex) {
                newindex = resloci[i];
                residx = i;
            }
        }
        int[] inarray = new int[2];
        inarray[0] = newindex;
        if(residx>=0) {
            resloci[residx] = chrseqbuilder.indexOf(resS[residx], start);
        }else {
            residx=0;
        }
        inarray[1] = residx;
        return inarray;
    }
}
