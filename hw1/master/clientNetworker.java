package os.hw1.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class clientNetworker extends Thread {
    static int port ;
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                clientRequestHandler eh = new clientRequestHandler(socket);
                eh.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
