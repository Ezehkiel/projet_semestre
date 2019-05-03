package ch.heigvd.pro.a03.socketServer;

import ch.heigvd.pro.a03.utils.Protocole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static ch.heigvd.pro.a03.utils.Communication.sendProtovol;

public class Worker implements Runnable{
    static ArrayList<ArrayList<Player>> waitingList;

    Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final int protcoleId=1;
    int nbPlayer;
    public Worker(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        nbPlayer =0;
    }

    @Override
    public void run() {
        try {
            if(in.readLine().equals("100-START")){
                sendProtovol(out,1,"OK");
                int gameMode = Integer.parseInt(Protocole.receive(in).getData());
                sendProtovol(out,1,"OK");
                sendProtovol(out,1,"END");
                while (!Protocole.receive(in).getData().equals("200-START"));
                Player p = new Player(socket);
                waitingList.get(gameMode).add(p);
                if(waitingList.get(gameMode).size()==1){
                    sendProtovol(out,2,"WAITINGPLAYER");
                }else{
                    sendProtovol(out,2,"PLAYERFOUND");
                }
                if(canLaunchaGame(gameMode)){
                    new Thread(new GameServer(waitingList.get(gameMode))).start();
                    waitingList.get(gameMode).clear();
                    sendProtovol(out,2,"END");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        nbPlayer =2;
    }

    private boolean canLaunchaGame(int mode) {
        return waitingList.get(mode).size() == mode;
    }

    public int getNbPlayer() {
        return nbPlayer;
    }
}
