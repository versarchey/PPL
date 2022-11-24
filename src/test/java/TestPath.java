import com.google.common.collect.Range;
import org.junit.Test;
import process.Path;
import process.contact.AlignTable2Contact;
import process.contact.mygraph.Edge;
import process.contact.mygraph.ListDirectGraph;
import process.mapping.Bed2AlignTable;
import utils.Contact2HicMedium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestPath {
    Path p = new Path();

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

    @Test
    public void testPath(){
        System.out.println("用户的当前工作目录:\t"+System.getProperty("user.dir"));
        System.out.println("用户的主目录:\t"+System.getProperty("user.home"));
        System.out.println("文件分隔符（在 UNIX 系统中是“/”）:\t"+System.getProperty("file.separator"));
        System.out.println("路径分隔符（在 UNIX 系统中是“:”）:\t"+System.getProperty("path.separator"));

    }

    @Test
    public void testPath2(){
        String PROGRAM_DIRECTORY = (new Path()).getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println(PROGRAM_DIRECTORY);
    }

    @Test
    public void test() throws IOException {
        Bed2AlignTable.main(new String[]{
                "/mnt/f/OneDrive/work/pore-C/test_out/SRR11589402/SRR11589402.bed",
                "/mnt/f/OneDrive/work/pore-C/test_out/SRR11589402/SRR11589402",
                "1",
                "res"
        });
    }

    @Test
    public void test2() throws IOException {
        ListDirectGraph<String> graph = new ListDirectGraph<>();
        graph.addVertex("zjwang");
        graph.addVertex("jj1");
        graph.addVertex("jj2");
        graph.addVertex("jj3");
        graph.addEdge(new Edge<String>("zjwang","jj1",100));
        graph.addEdge(new Edge<String>("zjwang","jj3",10));
        graph.addEdge(new Edge<String>("jj3","jj2",10));
        graph.addEdge(new Edge<String>("jj2","jj1",10));





    }

    @Test
    public void test3() throws IOException {
    }

    @Test
    public void test4() throws IOException {
        Contact2HicMedium.main(new String[]{
                "/mnt/f/OneDrive/work/pore-C/test_out/SRR11589402/SRR11589402.monomers",
                "/mnt/f/OneDrive/work/pore-C/test_out/SRR11589402/SRR11589402.pair",
                "10000"
        });
    }

    @Test
    public void test5(){
        HashMap<Range<Long>, String> map = new HashMap<>();
        Range<Long> ll;
        ll=Range.closed((long
                )1, (long)5);
        Range<Long> kk;
        kk=Range.closed((long
                )3, (long)5);
        System.out.println(ll.isConnected(kk));
    }

    @Test
    public void testStringEmpty(){
        System.out.println("".isEmpty());
    }
}
