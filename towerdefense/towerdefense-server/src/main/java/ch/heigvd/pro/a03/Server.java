package ch.heigvd.pro.a03;

import ch.heigvd.pro.a03.httpServer.HttpServer;
import ch.heigvd.pro.a03.socketServer.SocketServer;
import ch.heigvd.pro.a03.utils.Protocole;

import static spark.Spark.secure;

public class Server{



    public static void main(String[] args) {

        String keyStoreLocation = "towerdefense-server/deploy/keystore.jks";
        String keyStorePassword = "pro2019heig";
        secure(keyStoreLocation, keyStorePassword, null, null);
        // Run HTTP on other thread;
        new Thread(new HttpServer(Protocole.HTTPSERVERPORT)).start();
        new Thread(new SocketServer(Protocole.SOCKETSERVERPORT)).start();
    }
}
