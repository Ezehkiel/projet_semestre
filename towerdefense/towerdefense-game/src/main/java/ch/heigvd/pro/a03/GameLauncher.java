package ch.heigvd.pro.a03;

import ch.heigvd.pro.a03.scenes.MainMenuScene;
import ch.heigvd.pro.a03.scenes.SceneManager;
import ch.heigvd.pro.a03.users.User;
import ch.heigvd.pro.a03.utils.Config;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

/**
 * The Game launcher starts the game and creates the scene manager.
 */
public class GameLauncher implements ApplicationListener {

    static private GameLauncher instance = null;

    static public final String TITLE = "Tower Defense";
    static public final int WIDTH = 1280;
    static public final int HEIGHT = 768;

    private Music music;

    private User connectedPlayer;

    private SceneManager sceneManager;

    private Music mp3Music;

    /**
     * Default constructor, called by getInstance() if needed
     */
    private GameLauncher() {
        super();

        connectedPlayer = null;
        sceneManager = new SceneManager();


    }

    /**
     * Get the instance of the Singleton
     * @return the instance of GameLauncher
     */
    static public GameLauncher getInstance() {

        if (instance == null) {
            instance = new GameLauncher();
        }

        return instance;
    }

    /**
     * Get the connected player.
     * @return connect player
     */
    public User getConnectedPlayer() {
        return connectedPlayer;
    }

    /**
     * Setts the connected player.
     * @param connectedPlayer player to connect
     */
    public void setConnectedPlayer(User connectedPlayer) {
        this.connectedPlayer = connectedPlayer;
    }

    /**
     * Gets the scene manager.
     * @return scene manager
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    @Override
    public void create () {

        music = Gdx.audio.newMusic(Gdx.files.internal("assets/Distant Tales .mp3"));
        music.setLooping(true);
        updateMusic();

        sceneManager.add(new MainMenuScene());
    }

    /**
     * Starts or stop the music depend of the configs.
     */
    public void updateMusic() {
        if (Boolean.valueOf(Config.getProperty("music-on"))) {
            music.play();
        } else {
            music.stop();
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (sceneManager.hasScene()) {
            sceneManager.peek().update(Gdx.graphics.getDeltaTime());
            sceneManager.peek().draw();
        }
    }

    @Override
    public void dispose () {

        // remove all scenes
        while (sceneManager.hasScene()) {
            sceneManager.pop();
        }
    }

    @Override
    public void resize(int width, int height) {

        if (sceneManager.hasScene()) {
            sceneManager.peek().resize(width, height);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    /**
     * Closes every scenes and exits the application
     */
    public void exit() {

        while (sceneManager.hasScene()) {
            sceneManager.pop();
        }

        Gdx.app.exit();
    }

    /**
     * Application entry point
     * @param args arguments
     */
    public static void main(String[] args) {

        // Change Logger format
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = GameLauncher.TITLE;
        config.width = GameLauncher.WIDTH;
        config.height = GameLauncher.HEIGHT;

        new LwjglApplication(GameLauncher.getInstance(), config);
    }
}
