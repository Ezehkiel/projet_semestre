package ch.heigvd.pro.a03.warentities.units;

import ch.heigvd.pro.a03.warentities.WarEntityType;

import java.awt.*;

/***
 * Class representing a Scoot
 * @Author Andres Moreno
 */
public class Scoot extends Unit {

    /***
     * Constructor
     * @param name the unit name
     * @param position the position at the grid
     * @param totalHealth total health points
     * @param defPoint defense points
     * @param attackCoolDown attack cool down
     * @param attackPoints attack points
     * @param range the range of attack
     * @param price the cost in order to buy it
     */
    public Scoot(String name, Point position, int totalHealth,
                 int defPoint, int attackCoolDown, int speed,
                 int attackPoints, int range, int price) {

        super(name, position, totalHealth,
                defPoint, attackCoolDown, speed,
                attackPoints, range, price, WarEntityType.UnitType.SCOOT);

    }

    /***
     * Constructor
     * @param position the position at the grid
     */
    public Scoot(Point position){
        this("Soldier", position,350,150,5,
                3,100,2,100);
    }

    @Override
    public String symbol() {
        if(isEntityDestroyed()) return " X ";
        else return "Sct";
    }

}