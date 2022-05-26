package os.hw1.master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CacheServer {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;
    static List<String> operation;
    static List<Integer> result;


    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                Socket socket = serverSocket.accept();
                CacheServer cacheServer = new CacheServer(new DataInputStream(socket.getInputStream()),new DataOutputStream(socket.getOutputStream()),socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CacheServer(DataInputStream dis, DataOutputStream dos, Socket socket) {
        this.dis = dis;
        this.dos = dos;
        this.socket = socket;
        operation=new ArrayList<>();
        result=new ArrayList<>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageHandler();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public void messageHandler() throws IOException {
        while(true){
            String s=dis.readUTF();
            if(s.equals("check")){

                String temp=dis.readUTF();

                if(!search(temp)){

                    dos.writeUTF("No");
                }else{
                    dos.writeUTF("Yes");
                }
            }else if(s.equals("Ret")){
                String temp=dis.readUTF();
                dos.writeUTF(String.valueOf(searchAndReturn(temp)));

            }else if(s.equals("Add")){
                String temp=dis.readUTF();
                operation.add(temp);
                int res=Integer.parseInt(dis.readUTF());
                result.add(res);

            }

        }
    }
    public Boolean search(String s){
        for (int i = 0; i < operation.size(); i++) {
            if(operation.get(i).equals(s)){
                return true;
            }
        }
        return false;
    }
    public int searchAndReturn(String s){
        for (int i = 0; i < operation.size(); i++) {
            if(operation.get(i).equals(s)){
                return result.get(i);
            }
        }
        return -1;
    }

}
