package ch.heigvd.pro.a03.event.simulation;

public class AttackEvent extends SimEvent  {
    int targetId;
    int dammage;

    public AttackEvent(int ticId, int entityId, int targetId, int dammage,int map_id) {
        super(ticId, SimEventType.ATTACK, entityId, map_id);
        this.targetId = targetId;
        this.dammage = dammage;
    }

    @Override
    public String toString() {
        return super.toString() + ", target: " + targetId + ", damage: " + dammage;
    }
}
