package ch.heigvd.pro.a03.socketServer;


import javax.naming.ldap.SortKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


// RES Exemple
public class SocketServer implements Runnable{

    final static Logger LOG = Logger.getLogger(SocketServer.class.getName());
    ArrayList<GameServer> gameServers;
    int port;



    public SocketServer(int port) {
        this.port = port;
        gameServers = new ArrayList<>();
    }

    public void run() {
        LOG.info("Starting the socket server on port : " +port);
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
            try {

                Socket clientSocket = serverSocket.accept();

                new Thread(new Worker(clientSocket)).start();

                clientSocket.close();

            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}