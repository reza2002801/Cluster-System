package os.hw1.master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker extends Thread {
    static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private boolean working;
    static volatile boolean running;
    private int port;
    private Socket socket;
    public static DataInputStream dis;
    public static DataOutputStream dos;
    static List<String> commands;
    static List<String> programs;
    static List<Integer> programsWeights;
    static volatile List<workS> works;

    public Worker(int port) {
        this.port = port;

    }

    public static void main(String[] args) throws IOException {



        commands=new ArrayList<String>();
        programs=new ArrayList<String>();
        programsWeights=new ArrayList<Integer>();

        int numCommands = Integer.parseInt(args[0]);
        for (int i = 1; i < numCommands+1; i++) {
            commands.add(args[i]);
        }

        int numPrograms=Integer.parseInt(args[numCommands+1]);
        for (int i = numCommands+2; i < numCommands+numPrograms+2; i++) {
            programs.add(args[i]);
        }
        for (int i = numCommands+2+numPrograms+1; i < numCommands+numPrograms+numPrograms+3; i++) {
            programsWeights.add(Integer.parseInt(args[i]));
        }
        int port1=Integer.parseInt(args[args.length - 1]);
//        int port = 8000;
        Worker worker = new Worker(port1);

        worker.start();


    }
    public void start(){

        works=new ArrayList<workS>();
        running = true;

        try {
            establishConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listen();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    static void doWork(workS w) throws IOException {
        Logger.log(" !!! worker send 2241124 "+w.toString());
        int last=w.getFunctions().size();
        ProcessBuilder p=exec(programs.get(w.getFunctions().get(last-1)-1),commands);
        int weight=programsWeights.get(w.getFunctions().get(last-1)-1);
        Process c=p.start();
        PrintStream printStream = new PrintStream(c.getOutputStream());
        Scanner scanner = new Scanner(c.getInputStream());
        printStream.println(w.getInput());
        printStream.flush();
        int res=Integer.parseInt(scanner.nextLine());

//        Logger.log(" !!! worker send "+w.toString());
        w.updateWork(res);
//        synchronized(dos){
        dos.writeUTF("result");
        dos.writeUTF(serializer.toString(w));
        dos.writeUTF(String.valueOf(weight));
//        }
    }

    public static ProcessBuilder exec(String className,List<String> com) throws IOException {
        List<String> t = new ArrayList<>();
        t.addAll(com);
        t.add(className);
        return new ProcessBuilder(t);
    }



    private void checkCounter(int count) {
        if (count < 5) {
            return;
        }

        Random random = new Random();
        if (random.nextInt(8) == 1) {
            working = false;
        }
    }

    private void establishConnection() throws IOException {
        this.socket = new Socket(InetAddress.getLocalHost(), port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

    }


    private void listen() throws IOException, ClassNotFoundException {
        while (running) {
            String message = dis.readUTF();
            handleMessage(message);
        }
    }

    private void handleMessage(String message) throws IOException, ClassNotFoundException {
        if (message.equals("status")) {
        }else if(message.equals("work")){
//            Logger.log("!!! In Worker get ");
            String s=dis.readUTF();
//            Logger.log("!!! In Worker get 00");
            workS t=(workS) serializer.fromString(s);
//            Logger.log("!!! In Worker get "+t.toString());
            executorService.submit(()->{
                try {
                    doWork(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
