package ch.heigvd.pro.a03.socketServer.state;

import ch.heigvd.pro.a03.socketServer.GameServer;

public class ValidationState extends ServerState{
    public ValidationState(int id, GameServer gameServer) {
        super(id, gameServer);
    }

    @Override
    public void run() {
        gameServer.broadCastMessage("READY");

        GameServer.LOG.info("Wait for clients to be ready.");
        try {
            gameServer.waitForPlayers(getId() + "00-YES");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameServer.setCurrentState(gameServer.FirstRoundState);
    }
}