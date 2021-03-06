package ch.heigvd.pro.a03.menus.game;

import ch.heigvd.pro.a03.commands.ButtonCommand;
import ch.heigvd.pro.a03.commands.Command;
import ch.heigvd.pro.a03.commands.MacroCommand;
import ch.heigvd.pro.a03.menus.WindowMenu;
import ch.heigvd.pro.a03.utils.UI;
import ch.heigvd.pro.a03.warentities.WarEntityType;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.awt.*;

/**
 * Unit menu
 */
public class UnitMenu extends WindowMenu {

    private int count;
    private Label countLabel;
    private Label nameLabel;
    public final WarEntityType.UnitType UNIT_TYPE;
    public final int PRICE;
    private VerticalGroup addRemoveButtons;

    /**
     * Create the menu
     * @param parent  unit selection menu
     * @param unitType unit type
     * @param skin skin used
     */
    public UnitMenu(UnitSelectionMenu parent, WarEntityType.UnitType unitType, Skin skin) {
        super(skin);

        UNIT_TYPE = unitType;
        PRICE = UNIT_TYPE.createUnit(new Point()).getPrice();

        nameLabel = new Label(unitType.getName() + " " + PRICE + ".-", skin);

        Command<UnitSelectionMenu> updateParentCommand = new Command<UnitSelectionMenu>(parent) {
            @Override
            public void execute(Object... args) {
                getReceiver().updatePrice();
            }
        };

        Button plusButton = new Button(skin, "arrow-up");
        plusButton.addListener(new ButtonCommand(new MacroCommand(
                new Command<UnitMenu>(this) {
                    @Override
                    public void execute(Object... args) {
                        getReceiver().addUnit();
                    }
                },
                updateParentCommand
        )));

        Button minusButton = new Button(skin, "arrow-down");
        minusButton.addListener(new ButtonCommand(new MacroCommand(
                new Command<UnitMenu>(this) {
                    @Override
                    public void execute(Object... args) {
                        getReceiver().removeUnit();
                    }
                },
                updateParentCommand
        )));

        count = 0;
        countLabel = new Label("0", skin);
        countLabel.setAlignment(Align.right);

        addRemoveButtons = new VerticalGroup();
        addRemoveButtons.align(Align.center);
        addRemoveButtons.addActor(plusButton);
        addRemoveButtons.addActor(minusButton);

        getWindow().defaults().prefHeight(UI.BUTTON_HEIGHT);
        getWindow().add(nameLabel).prefWidth(UI.BUTTON_SMALL_WIDTH);
        getWindow().add(addRemoveButtons);
        getWindow().add(countLabel).expandY().grow();
    }

    /**
     * Update the count label
     */
    private void updateCountLabel() {
        countLabel.setText(count);
    }

    /**
     * increment the count by one
     */
    public void addUnit() {
        count++;
        updateCountLabel();
    }

    /**
     * decrement the count by one
     */
    public void removeUnit() {

        if (count <= 0) {
            return;
        }

        count--;
        updateCountLabel();
    }

    /**
     * Gets the count.
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Disable the add and remove buttons
     */
    public void disable() {

        addRemoveButtons.setVisible(false);
    }

    /**
     * resets the count to zero and show the add and remove buttons
     */
    public void reset() {
        count = 0;
        updateCountLabel();
        addRemoveButtons.setVisible(true);
    }
}
