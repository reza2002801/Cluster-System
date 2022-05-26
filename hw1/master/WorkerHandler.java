package os.hw1.master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static os.hw1.master.MainServer.cacheServerHandler;
import static os.hw1.master.MainServer.sendedQ;

public class WorkerHandler {
    private int id;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private int w;
    public WorkerHandler(int id, Socket clientSocket, DataInputStream dis, DataOutputStream dos) throws IOException {
        this.w=0;
        this.id = id;
        this.clientSocket = clientSocket;
        this.dis = dis;
        this.dos = dos;

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Reciever();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public String pingWorker() throws IOException {

        String request = "status";
        try {
            sendRequest(request);
            return listenForResponse();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void Reciever() throws IOException, ClassNotFoundException {
        while (true) {
            String result=dis.readUTF();
            if(result.equals("status")){
                String t=dis.readUTF();
            }else if(result.equals("result")){
                work t=workSTowork((workS)serializer.fromString(dis.readUTF()));
//                    MainServer.recievedQ.add(t);
                work w= (work) findInSended(t);
//                    Logger.log("    reciever   "+t.toString());
                    cacheServerHandler.updateCache(String.valueOf(w.getFunctions().get(w.getFunctions().size()-1))+" "+String.valueOf(w.getInput()),String.valueOf(t.getInput()));
                    synchronized(sendedQ) {
                        updateNeededQ(w, t);
                    }
                this.w-=Integer.parseInt(dis.readUTF());
            }

        }
    }
    private void updateNeededQ(work work,work Rwork) throws IOException {
//shoul be synched
        int c=work.lastF();
        int p=work.getInput();
//        Logger.log("    Seeeeeeeeeeeeeeeee 4  "+work);
        List<work> removed=new ArrayList<>();
        for (int i = 0; i < MainServer.sendedQ.size(); i++){
            if(p==MainServer.sendedQ.get(i).getInput() && c==MainServer.sendedQ.get(i).lastF()){
                MainServer.sendedQ.get(i).updateWork(Rwork.getInput());
                removed.add(MainServer.sendedQ.get(i));
            }
        }
//        Logger.log("    Seeeeeeeeeeeeeeeee   "+MainServer.sendedQ);
//        Logger.log("    Seeeeeeeeeeeeeeeee 3  "+removed+work);
        for (int i = 0; i < removed.size(); i++){
            MainServer.sendedQ.remove(removed.get(i));
        }
//        Logger.log("    Seeeeeeeeeeeeeeeee2   "+MainServer.sendedQ);
        for (int i = 0; i < removed.size(); i++) {
            if (removed.get(i).getFunctions().isEmpty()) {
                removed.get(i).getCrh().setOutput(removed.get(i).getInput());
                removed.get(i).getCrh().resultsReady.signal();
            }else{
//                synchronized(MainServer.workQ) {
                    orderWorkQ(removed.get(i));
//                }
            }

        }
    }
    private void orderWorkQ(work work) {
        for (int i = 0; i < MainServer.workQ.size(); i++) {
            if(work.getTime()<MainServer.workQ.get(i).getTime()){
                MainServer.workQ.add(i,work);
                return;
            }
        }
        MainServer.workQ.add(work);
    }
    private work findInSended(work work){
        for (int i = 0; i < MainServer.sendedQ.size(); i++) {
            if(work.getId()==MainServer.sendedQ.get(i).getId()){
                return MainServer.sendedQ.get(i);
            }
        }
        return null;
    }
    public work workSTowork(workS ws){
        for (int i = 0; i < MainServer.sendedQ.size(); i++) {

            if(ws.getId()==MainServer.sendedQ.get(i).getId()){

                return new work(ws.getS(),MainServer.sendedQ.get(i).getCrh(),ws.getTime(),ws.getId(),ws.getFunctions(),ws.getInput());
            }

        }
        return null;
    }



    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    private void sendRequest(String request) throws IOException {
        dos.writeUTF(request);
    }

    private String listenForResponse() throws IOException {
        return dis.readUTF();
    }



    public void sendWork(work work) throws IOException {
//        Logger.log("On platform "+work.toString());
        dos.writeUTF("work");
        dos.writeUTF(serializer.toString(new workS(work)));

    }
}
