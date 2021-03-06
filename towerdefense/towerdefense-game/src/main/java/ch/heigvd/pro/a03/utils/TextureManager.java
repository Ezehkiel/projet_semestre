package ch.heigvd.pro.a03.utils;

import ch.heigvd.pro.a03.warentities.WarEntityType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Manages the textures
 */
public class TextureManager {

    private static TextureManager instance;

    private Texture notFound;

    private Texture soldier;
    private Texture tank;
    private Texture scoot;

    /**
     * Constructor
     */
    private TextureManager() {

        notFound = new Texture(Gdx.files.internal("assets/NotFound.png"));

        soldier = new Texture(Gdx.files.internal("assets/units/Soldier.png"));

        tank = new Texture(Gdx.files.internal("assets/units/Tank.png"));

        scoot = new Texture(Gdx.files.internal("assets/units/Scoot.png"));
    }

    /**
     * Gets the instance of the singleton
     * @return texture manager
     */
    public static TextureManager getInstance() {

        if (instance == null) {
            instance = new TextureManager();
        }

        return instance;
    }

    /**
     * Gets the texture of a unit
     * @param type unit type
     * @return texture
     */
    public static Texture getUnitTexture(WarEntityType.UnitType type) {

        switch (type) {
            case SOLIDER:
                return getInstance().soldier;
            case TANK:
                return getInstance().tank;
            case SCOOT:
                return getInstance().scoot;
        }

        return getInstance().notFound;
    }
}
