package os.hw1.master;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class clientRequestHandler extends  Thread {

//    static Logger logger= LogManager.getLogger(clientRequestHandler.class);
//    public void setResultsReady(ThreadEvent resultsReady) {
//        this.resultsReady = resultsReady;
//    }

    ThreadEvent resultsReady = new ThreadEvent();
    public ThreadEvent getResultsReady() {
        return this.resultsReady;
    }
    private final Socket socket;
    private final Scanner scanner;
    private final PrintStream printStream;
    private work work;


    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    private int output;
    static volatile boolean run;

    public clientRequestHandler(Socket socket) throws  IOException {
        this.socket = socket;
        scanner = new Scanner(socket.getInputStream());
        printStream = new PrintStream(socket.getOutputStream());
    }

    @Override
    public void start() {
        super.start();
    }

    public void run() {
        try {
//            Logger.log("user request");
            this.output=0;
            String chain=scanner.nextLine();
            this.work=new work(chain,this,System.currentTimeMillis());
            synchronized(MainServer.workQ){
//                wait();
                MainServer.workQ.add(this.work);
//                notifyAll();
            }
//            while(run);
            resultsReady.await();
            printStream.println(this.output);
            printStream.flush();
            printStream.println("\n");
//            Logger.log("user get answer "+this.output+"for "+this.work.toString());
            this.socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
  }