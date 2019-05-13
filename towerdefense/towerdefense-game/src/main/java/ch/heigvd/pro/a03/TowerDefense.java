package ch.heigvd.pro.a03;

import ch.heigvd.pro.a03.algorithm.Astar;
import ch.heigvd.pro.a03.algorithm.Position;
import ch.heigvd.pro.a03.commands.Executable;
import ch.heigvd.pro.a03.commands.game.ShowMapsCommand;
import ch.heigvd.pro.a03.event.player.*;
import ch.heigvd.pro.a03.event.simulation.*;
import ch.heigvd.pro.a03.scenes.GameScene;
import ch.heigvd.pro.a03.server.GameClient;
import ch.heigvd.pro.a03.states.StateMachine;
import ch.heigvd.pro.a03.states.towerdefense.*;
import ch.heigvd.pro.a03.utils.Simulator;
import ch.heigvd.pro.a03.utils.Waiter;
import ch.heigvd.pro.a03.warentities.Base;

import ch.heigvd.pro.a03.warentities.Structure;
import ch.heigvd.pro.a03.warentities.WarEntityType;
import ch.heigvd.pro.a03.warentities.turrets.Turret;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.*;
import java.util.LinkedList;

public class TowerDefense {

    private GameScene scene;
    private GameClient gameClient;

    public static final int MAP_WIDTH = 9;
    public static final int MAP_HEIGHT = 12;

    private Map[] maps;

    // States variables
    private StateMachine stateMachine;
    private GameState[] states;

    private Executable roundStart;
    private Executable roundEnd;
    private Executable playerTurnStart;
    private Executable playerTurnEnd;
    private Executable showMaps;

    private PlayerEvent playerEvent;
    private Waiter<PlayerEvent> playerEventWaiter;
    public LinkedList<SimEvent> simEvents = null;

    private Simulator[] simulators;

    public enum GameStateType {
        FIRST_PLAY, PLAY, OPPONENT_PLAY, SIMULATION, WAIT
    }

    public TowerDefense(GameScene scene, GameClient gameClient) {

        this.scene = scene;
        this.gameClient = gameClient;
        simulators = new Simulator[gameClient.PLAYERS_COUNT];
        maps = new Map[gameClient.PLAYERS_COUNT];
        for (int i = 0; i < maps.length; ++i) {
            maps[i] = new Map(MAP_HEIGHT, MAP_WIDTH, new Base(new Point(4,11)),new Point(11,4), i);
        }

        stateMachine = new StateMachine();

        states = new GameState[] {
                new FirstPlayState(stateMachine, this),
                new PlayState(stateMachine, this),
                new OpponentPlayState(stateMachine, this),
                new SimulationState(stateMachine, this),
                new WaitState(stateMachine, this)
        };

        changeState(GameStateType.WAIT);

        playerEvent = new PlayerEvent();
        playerEventWaiter = new Waiter<>();

        // Setup commands
        playerTurnStart = args -> changeState(
                gameClient.getPlayer().ID == (Integer) args[0] ?
                        GameStateType.PLAY : GameStateType.OPPONENT_PLAY
        );
        playerTurnEnd = args -> changeState(GameStateType.WAIT);

        roundStart = args -> gameClient.round(playerTurnStart, playerTurnEnd, roundEnd, showMaps, playerEventWaiter);
        roundEnd = a -> gameClient.startSimulation(args -> {
            simEvents = (LinkedList<SimEvent>) args[0];
            setupSimulators();
            changeState(GameStateType.SIMULATION);
        });

        showMaps = new ShowMapsCommand(this);

        gameClient.firstRound(args -> changeState(
            gameClient.getPlayer().ID == (Integer) args[0] ?
                    GameStateType.FIRST_PLAY : GameStateType.OPPONENT_PLAY
        ), playerTurnEnd, roundStart, showMaps, playerEventWaiter);
    }

    public void endSimulation() {
        changeState(GameStateType.WAIT);
        gameClient.endSimulation(roundStart,
                args -> System.out.println("Game Ended. Loser: " + ((Player) args[0]).toJson()));
    }

    public void setupSimulators() {
        simulators = new Simulator[gameClient.PLAYERS_COUNT];
        for (Map map : maps) {
            simulators[map.ID] = new Simulator(map, gameClient.getPlayer().ID);
        }
    }

    public void updateSimulation(float deltaTime) {
        if (stateMachine.getState() instanceof SimulationState) {
            for (Simulator simulator : simulators) {
                simulator.update(deltaTime);
            }
        }
    }

    public void drawSimulation(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {

        if (stateMachine.getState() instanceof SimulationState) {
            spriteBatch.begin();
            for (Simulator simulator : simulators) {
                simulator.drawSprites(spriteBatch);
            }
            spriteBatch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.ORANGE);
            for (Simulator simulator : simulators) {
                simulator.drawShapes(shapeRenderer);
            }
            shapeRenderer.end();
        }
    }

    public void processSimEvent(SimEvent event) {

        GameClient.LOG.info(event.toString());

        switch (event.TYPE) {
            case SPAWN:
                simulators[event.MAP_ID].spawn((SpawnEvent) event);
                break;
            case MOVE:
                simulators[event.MAP_ID].move((MoveEvent) event);
                break;
            case ATTACK:
                simulators[event.MAP_ID].attack((AttackEvent) event);
                break;
            case DEATH:
                simulators[event.MAP_ID].death((DeathEvent) event);
                break;
        }
    }

    /* ----- Turret Management -----*/

    public boolean isCellOccupied(int mapId, int x, int y) {
        return maps[mapId].getStructureAt(y, x) != null;
    }

    public Turret getTurretAt(int mapId, int x, int y) {

        Structure structure = maps[mapId].getStructureAt(y, x);
        if (structure instanceof Turret) {
            return (Turret) structure;
        }

        return null;
    }

    public boolean placeTurret(int mapId, Turret turret) {

        if (!iAmMapOwner(mapId) || !isInState(GameStateType.PLAY) ||
                maps[mapId].getStructureAt(turret.getPosition().y, turret.getPosition().x) != null) {
            return false;
        }

        if (turret.getPosition().x == maps[mapId].getSpawnPoint().x &&
                turret.getPosition().y == maps[mapId].getSpawnPoint().y) {
            return false;
        }

        if (gameClient.getPlayer().getMoney() < turret.getPrice()) {
            return false;
        }

        try {
            maps[mapId].setStructureAt(turret, turret.getPosition().y, turret.getPosition().x);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        // Check for path
        Map map = maps[mapId];
        Astar pathFinding = new Astar(map.getRow(), map.getCol(),
                new Position(map.getSpawnPoint().y, map.getSpawnPoint().x),
                new Position(map.getBase().getPosition().y, map.getBase().getPosition().x));

        Structure[][] blockage = map.getStructures();

        // Sets the blockage
        for (int i = 0; i < blockage.length; ++i) {
            for (int j = 0; j < blockage[i].length; ++j) {
                if (blockage[i][j] != null) {
                    if (blockage[i][j] != map.getBase()) {
                        pathFinding.setBlockPos(blockage[i][j].getPosition().y,
                                blockage[i][j].getPosition().x);
                    }
                }
            }
        }

        if (pathFinding.findPath().isEmpty()) {
            try {
                maps[mapId].setStructureAt(null, turret.getPosition().y, turret.getPosition().x);
            } catch (IndexOutOfBoundsException e) {
                return false;
            }

            return false;
        }

        playerEvent.addTurretEvent(new TurretEvent(
                TurretEventType.ADD, turret.getPosition(), turret.TYPE
        ));

        gameClient.getPlayer().removeMoney(turret.getPrice());
        scene.getGameMenu().updateMoney(gameClient.getPlayer().getMoney());

        scene.updateMaps();

        return true;
    }

    public boolean repairTurret(Turret turret) {

        if (!turret.isEntityDestroyed() || gameClient.getPlayer().getMoney() < turret.getPrice() / 2) {
            return false;
        }

        playerEvent.addTurretEvent(new TurretEvent(
                TurretEventType.REPAIR, turret.getPosition(), turret.TYPE
        ));

        turret.heal(turret.getTotalHealth());
        gameClient.getPlayer().removeMoney(turret.getPrice() / 2);
        scene.getGameMenu().updateMoney(gameClient.getPlayer().getMoney());

        return true;
    }

    public boolean destroyTurret(int mapId, Turret turret) {

        if (!iAmMapOwner(mapId) || !isInState(GameStateType.PLAY) ||
                maps[mapId].getStructureAt(turret.getPosition().y, turret.getPosition().x) == null) {
            return false;
        }

        try {
            maps[mapId].setStructureAt(null, turret.getPosition().y, turret.getPosition().x);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        playerEvent.addTurretEvent(new TurretEvent(
                TurretEventType.DESTROY, turret.getPosition(), turret.TYPE
        ));

        gameClient.getPlayer().removeMoney(turret.getPrice() / 2);
        scene.getGameMenu().updateMoney(gameClient.getPlayer().getMoney());

        scene.updateMaps();

        return true;
    }

    public boolean iAmMapOwner(int mapId) {
        return mapId == gameClient.getPlayer().ID;
    }

    public Map[] getMaps() {
        return maps;
    }

    private boolean isInState(GameStateType stateType) {
        return stateMachine.getState() == getState(stateType);
    }

    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public GameState getState(GameStateType stateType) {
        return states[stateType.ordinal()];
    }

    public GameScene getScene() {
        return scene;
    }

    public boolean changeState(GameStateType stateType) {
        return stateMachine.changeState(getState(stateType));
    }

    public void sendEvents() {
        playerEventWaiter.send(playerEvent);
    }

    public void clearPlayerEvents() {
        playerEvent = new PlayerEvent();
    }

    public boolean sendUnits(WarEntityType.UnitType[] types, int[] quantities) {

        if (types.length != quantities.length) {
            return false;
        }

        Point dummyPoint = new Point();
        int totalPrice = 0;

        for (int i = 0; i < types.length; ++i) {
            totalPrice += types[i].createUnit(dummyPoint).getPrice() * quantities[i];
        }

        Player player = gameClient.getPlayer();

        if (totalPrice > player.getMoney()) {
            return false;
        }

        player.removeMoney(totalPrice);
        scene.getGameMenu().updateMoney(player.getMoney());

        for (int i = 0; i < types.length; ++i) {
            for (int id : gameClient.getOpponentsIds()) {
                if (quantities[i] > 0) {
                    playerEvent.addUnitEvent(new SendUnitEvent(id, types[i], quantities[i]));
                }
            }
        }

        return true;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public void setMap(int i, Map map) {
        maps[i] = map;
    }
}
