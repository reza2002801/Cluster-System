package os.hw1.master;




//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class test {
    private Socket socket;
    private Scanner dis;
    private PrintStream dos;
//     static Logger logger= LogManager.getLogger(test.class);
    public test() throws IOException {
        socket = new Socket("localhost",16543);
        dis = new Scanner(socket.getInputStream());
        dos = new PrintStream(socket.getOutputStream());
    }
    static volatile boolean b;
    static final ExecutorService executorService = Executors.newFixedThreadPool(50);
    static final String[] commonArgs = {
            "C:\\Program Files\\AdoptOpenJDK\\jdk-11.0.11.9-hotspot\\bin\\java.exe ", // replace with your java path with version 1.8
            "-classpath",
            "C:\\Users\\NoteBook TANDIS\\intelijProjects\\OSProject12\\out\\production\\OSProject12" // replace with your classpath
    };  static final String[] programs = {
            "os.hw1.programs.Program1 2",
            "os.hw1.programs.Program2 3",
    };
    static int port=16543;
    public static Integer tttt(String s) throws IOException {

        Socket socket = new Socket(InetAddress.getLocalHost(), port);
        Scanner scanner = new Scanner(socket.getInputStream());
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        printStream.println(s);
        printStream.flush();
        int response = scanner.nextInt();
        socket.close();
        return response;

    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        logger.debug("in loadImageBigger from BiggerProfileController class on values");
//        logger.info("sask");
//        test t=new test();
//        t.tttt("1|2|1|2|1 50");
        Future<Integer> r1 = executorService.submit(() -> tttt("1|2|1|2|1 50"));
        Thread.sleep(100);
        Future<Integer> r2 = executorService.submit(() -> tttt("1|1|1|1|1 50"));
        Thread.sleep(100);
        Future<Integer> r3 = executorService.submit(() -> tttt("1|1|1|2|1 50"));
        int a1 = r1.get();
        int a2 = r2.get();
        int a3 = r3.get();
        System.out.println(a1);
        System.out.println(a2);
        System.out.println(a3);
        executorService.shutdown();
////            Process process = new ProcessBuilder(
////                    commonArgs[0], commonArgs[1], commonArgs[2], "os.hw1.master.MasterMain"
////            ).start();
//        System.out.println(System.currentTimeMillis());
//        System.out.println(System.currentTimeMillis());


    }
}


