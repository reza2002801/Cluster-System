package os.hw1.master;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class CacheServerHandler {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    public CacheServerHandler() throws IOException {
        this.socket = new Socket(InetAddress.getLocalHost(),9500);
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }
    public synchronized boolean checkExist(String s) throws IOException {

        dos.writeUTF("check");

        dos.writeUTF(s);


        String res=dis.readUTF();

        if(res.equals("No")){
            return false;
        }else{
            return true;
        }
    }
    public void updateCache(String s,String res) throws IOException {
        dos.writeUTF("Add");
        dos.writeUTF(s);
        dos.writeUTF(res);
    }


    public synchronized int getExist(String s) throws IOException {
        dos.writeUTF("Ret");
        dos.writeUTF(s);
        return Integer.parseInt(dis.readUTF());
    }
}
