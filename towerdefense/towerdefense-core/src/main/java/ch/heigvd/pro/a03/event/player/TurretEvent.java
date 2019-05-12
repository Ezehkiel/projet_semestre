package ch.heigvd.pro.a03.event.player;

import ch.heigvd.pro.a03.event.Event;
import ch.heigvd.pro.a03.warentities.WarEntityType;

import java.awt.*;

public class TurretEvent extends Event {

    TurretEventType turretEventType;
    Point turretPosition;
    WarEntityType.TurretType turretType;

    public TurretEvent( TurretEventType turretEventType, Point turretPosition, WarEntityType.TurretType turretType) {
        this.turretEventType = turretEventType;
        this.turretPosition = turretPosition;
        this.turretType = turretType;
    }

    public TurretEventType getTurretEventType() {
        return turretEventType;
    }

    public Point getTurretPosition() {
        return turretPosition;
    }

    public WarEntityType.TurretType getTurretType() {
        return turretType;
    }
}
