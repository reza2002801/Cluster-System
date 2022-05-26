package os.hw1.master;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer implements Runnable {

    public static volatile  List<work> workQ;
    public static volatile List<work> sendedQ;
    private ServerSocket serverSocket;
    private int port;
    private volatile boolean running;
    private Socket clientSocket;
    private List<WorkerHandler> workerHandlers;
    static CacheServerHandler cacheServerHandler;

    public MainServer(int port) throws IOException {
        workQ=new ArrayList<>();
        sendedQ=new ArrayList<>();
        this.port = port;
        cacheServerHandler=new CacheServerHandler();
        this.workerHandlers = new ArrayList<>();
    }
    @Override
    public void run() {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (running){
                    try {
                        sender();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        try{
            listenForNewConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
     private void updateWorkQ() throws Exception {
        boolean run=true;
        while (run){
            run=false;
//shoul be synched
                for (int i = 0; i < workQ.size(); i++){
                    String s=String.valueOf(
                            workQ.get(i).lastF())+" "+String.valueOf(workQ.get(i).getInput());
                    if(cacheServerHandler.checkExist(s)){
                        workQ.get(i).updateWork(cacheServerHandler.getExist(s));
                        run=true;
                    }
                }
                List<work> removed=new ArrayList<>();
                for (int i = 0; i < workQ.size(); i++){
                    if(workQ.get(i).getFunctions().isEmpty()){
                        workQ.get(i).getCrh().setOutput(workQ.get(i).getInput());
                        workQ.get(i).getCrh().resultsReady.signal();
                        removed.add(workQ.get(i));
                    }
                }
                for (int i = 0; i < removed.size(); i++){
                    workQ.remove(removed.get(i));
                }
        }
    }

    private void sender() throws Exception {
            while(!workQ.isEmpty()){
                synchronized(workQ) {
//                    Logger.log("sender   " + workQ.get(0).toString());

                        updateWorkQ();

                    if (workQ.isEmpty()) continue;
                    //check in cache
                    //if yes return
                    if (isInSendedQ(workQ.get(0))) {
                        sendedQ.add(workQ.get(0));
                        workQ.remove(0);
//                        Logger.log("sendddd" + sendedQ);
                    } else {
//                        Logger.log("sendddd" + sendedQ);
                        while (!canSend(workQ.get(0))) ;
                        synchronized(sendedQ) {
                            sendWork(workQ.get(0));
                        }
                    }
                }
            //check in sendedQ
                //if yes put in neededQ
            //wait untill there is a server to run this work
        }
    }


    private void sendWork(work work) throws IOException {
        int pw=MasterMain.programweights.get(work.getFunctions().get(work.getFunctions().size()-1)-1);
        int min=160000;
        WorkerHandler ww=null;
        for (WorkerHandler workerHandler : workerHandlers) {
            if(workerHandler.getW()<min){
                min=workerHandler.getW();
                ww=workerHandler;
            }
        }
        ww.setW(ww.getW()+pw);
        ww.sendWork(work);

        sendedQ.add(workQ.get(0));
        workQ.remove(0);
    }


    private boolean isInSendedQ(work work){
        for (int i = 0; i < sendedQ.size(); i++) {
            if(work.getInput()==sendedQ.get(i).getInput() &&
                    work.lastF()==sendedQ.get(i).lastF()){
                return true;
            }
        }
        return false;
    }


    private boolean canSend(work work){
        int pw=MasterMain.programweights.get(work.getFunctions().get(work.getFunctions().size()-1)-1);
        int min= 16000;
        for (WorkerHandler workerHandler : workerHandlers) {
            if(workerHandler.getW()<min){
                min=workerHandler.getW();
            }
        }
        int temp=min;
        int remain=MasterMain.maxW-temp;
        if(remain<pw){
            return false;
        }
        else {
            return true;
        }
    }

    private void listenForNewConnection() throws IOException {
        while (running) {
            try {
                clientSocket = this.serverSocket.accept();
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                int id = createUid();
                WorkerHandler workerHandler = new WorkerHandler(id, clientSocket, dis, dos);
                workerHandlers.add(workerHandler);

            } catch (IOException e) {
                clientSocket.close();
                e.printStackTrace();
            }
        }
    }
    private int createUid() {
        Random rand = new Random();
        int maxNumber = 9999;
        int minNumber = 1000;

        return rand.nextInt(maxNumber) + minNumber;
    }
    public void start() {
        try {
            establishServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.running = true;

        Thread thread = new Thread(this);
        thread.start();
    }

    private void establishServer() throws IOException {
        serverSocket = new ServerSocket(port);
    }

}
