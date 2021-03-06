package ch.heigvd.pro.a03.algorithm;


import ch.heigvd.pro.a03.Map;
import ch.heigvd.pro.a03.warentities.Structure;
import ch.heigvd.pro.a03.warentities.WarEntity;
import ch.heigvd.pro.a03.warentities.units.Unit;
import java.util.LinkedList;
import java.util.List;


/***
 * Class able to determine the nearest target.
 * @author Nicodeme Stalder
 */
public class NearestTarget {

    static boolean[][] isMarked;

    /**
     * @param warEntity The entity
     * @return the location of the nearest Structure in given range of the given unit
     * if their is none, null is returned
     */
    public static Structure getNearestInRangeStructure(WarEntity warEntity, Map map){

        //we adapt isMarked to the map size
         if(isMarked == null || isMarked.length != map.getRow() || isMarked[0].length != map.getCol())
             isMarked = new boolean[map.getRow()][map.getCol()];
         else cleanIsMarked();


        Vec2 startingPosition = new Vec2(warEntity.getPosition().y, warEntity.getPosition().x);
        int range = (int) warEntity.getRange();


        LinkedList<Vec2> queue = new LinkedList<>();
        queue.add(startingPosition);
        queue.add(null);
        isMarked[startingPosition.getRow()][startingPosition.getCol()] = true;
        for(int i = 0; i < range;){
            Vec2 next = queue.remove();
            if(next == null){
                queue.add(null);
                next = queue.remove();
                ++i;
            }
            List<Vec2> neighbours = getNeighboursOf(next, map);
            Vec2 target = getATargetIn(neighbours, map);
            if(target != null) return map.getStructureAt(target.getRow(), target.getCol());
            queue.addAll(neighbours);
        }
        return null;
    }

    /***
     *
     * @param warEntity the entity from where we try to get the nearest unit.
     * @param map the grid
     * @return the nearest Unit that is in range otherwise returns null.
     */
    public static Unit getNearestInRangeUnit(WarEntity warEntity, Map map){
        //find the chosen one if any (the chosenOne is the nearest unit in range of the warEntity)
        Unit chosenOne = null;
        int chosenOneDistance = Integer.MAX_VALUE;
        for(Unit u: map.getUnits()){
            if(!u.isEntityDestroyed() && warEntity.isInRange(u) && WarEntity.distance(warEntity, u) < chosenOneDistance){
                chosenOne = u;
                chosenOneDistance = WarEntity.distance(warEntity, u);
            }
        }
        return chosenOne;
    }

    /***
     *
     * @param p the position
     * @param map the gird
     * @return a list of Vec2 containing al the neighbours of the given position.
     */
    private static List<Vec2> getNeighboursOf(Vec2 p, Map map){
        List<Vec2> l = new LinkedList<>();
        int row = p.getRow();
        int col = p.getCol();

        if(row > 0 && !isMarked[row - 1][col]){
            l.add(new Vec2(row - 1, col));
            isMarked[row - 1][col] = true;
        }
        if(row < map.getRow() && !isMarked[row + 1][col]){
            l.add(new Vec2(row + 1, col));
            isMarked[row + 1][col] = true;
        }
        if(col > 0 && !isMarked[row][col - 1]){
            l.add(new Vec2(row, col - 1));
            isMarked[row][col - 1] = true;
        }
        if(col < map.getCol() && !isMarked[row][col + 1]){
            l.add(new Vec2(row, col + 1));
            isMarked[row][col + 1] = true;
        }

        return l;
    }

    /***
     *
     * @param ps  the list of positions
     * @param map the grid
     * @return returns the position of the closest target if it's still alive
     */
    private static Vec2 getATargetIn(List<Vec2> ps, Map map) {
        for(Vec2 p: ps) {
            Structure target = map.getStructureAt(p.getRow(), p.getCol());
            if(target != null && !target.isEntityDestroyed()) {
                return p;
            }
        }
        return null;
    }


    private static void cleanIsMarked(){
        for(int i = 0; i < isMarked.length; ++i)
            for(int j = 0; j < isMarked[0].length; ++j)
                isMarked[i][j] = false;
    }

}
