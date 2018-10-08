package game.mobs;

import engine.entities.Entity;
import engine.entities.LivingEntity;
import engine.util.ColorInterpolator;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector3f;
import pathfinding.A_star;
import sun.security.ssl.Debug;

import java.util.List;

public class SimpleMob extends LivingEntity {
    private A_star pathfinder = new A_star();
    private Tile currentTile, targetTile;
    private List<Tile> path;
    private int pathProgress = 0;
    private Entity target;

    public SimpleMob(Mesh mesh, Map map) {
        super(mesh, map);
    }

    public SimpleMob(Mesh mesh, Map map, Vector3f position, Vector3f rotation) {
        super(mesh, map, position, rotation);
    }

    public SimpleMob(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float speed) {
        super(mesh, map, position, rotation, speed);
    }

    public SimpleMob(Mesh mesh, Map map, Vector3f position, Vector3f rotation, float scale, float speed) {
        super(mesh, map, position, rotation, scale, speed);
    }

    public void setTarget(Entity entity) {
        target = entity;
    }

    boolean closeProximity = false;
    @Override
    public void update(float delta) {
        super.update(delta);
        // Calculate current mob tile and entity tile
        currentTile = super.getMap().getTile(Math.round(getPosition().x), Math.round(getPosition().z));
        if (target != null) {
            Tile targetCurrentTile = super.getMap().getTile(Math.round(target.getPosition().x), Math.round(target.getPosition().z));
            // Recalculate Path only if the target tile has changed
            if (targetCurrentTile != targetTile) {
                targetTile = targetCurrentTile;
                if (targetTile != null) {
                    path = findPathToTile(currentTile, targetTile);
                    pathProgress = 1;
                    closeProximity = false;
                }
            }

            // Calculate direction Vector
            Vector3f direction = new Vector3f(0, 0, 0);

            if (currentTile.getPosition() == path.get(pathProgress).getPosition()) {
                if (pathProgress < path.size() - 1) {
                    pathProgress++;
                    closeProximity = false;
                } else {
                    closeProximity = true;
                }
            }

            if (closeProximity) {
                direction = getDirectionVector(
                        new Vector3f(this.getPosition().z, 0, -this.getPosition().x),
                        new Vector3f(target.getPosition().z, 0, -target.getPosition().x));
            } else {
                direction = getDirectionVector(path.get(pathProgress), currentTile).mul(-1);
            }

            // Update Rotation
            setRotation(0, (float)Math.toDegrees(-Math.atan2(direction.z, direction.x)), 0);
            // Update Position
            Debug.println("Mob", direction.toString() + ", progress: " +
                    pathProgress + ", pos: " + position.toString() + ", size: " +
                    path.size() + ", cur(" + currentTile.getPosition().x + "," +
                    currentTile.getPosition().y + "), tar(" + path.get(pathProgress).getPosition().x +
                    "," + path.get(pathProgress).getPosition().y + "), (" +
                    path.get(path.size() - 1).getPosition().x + "," + path.get(path.size() - 1).getPosition().y + "), tarPos:" +
                    target.getPosition().toString());
            setPosition(new Vector3f(position).add(direction.mul(delta / getSpeed())));
        }
    }



    private boolean isTargetInLineOfSight() {
        return true;
    }

    private Vector3f getDirectionVector(Tile start, Tile end) {
        return new Vector3f(
                end.getPosition().x,
                0,
                end.getPosition().y).
                sub(new Vector3f(
                        start.getPosition().x,
                        0,
                        start.getPosition().y))
                .normalize();
    }

    private Vector3f getDirectionVector(Vector3f start, Vector3f end) {
        return new Vector3f(
                end.x,
                0,
                end.z)
                .sub(new Vector3f(
                        start.x,
                        0,
                        start.z))
                .normalize();
    }

    private List<Tile> findPathToTile(Tile start, Tile target) {
        return pathfinder.computePath(start, target, super.getMap());
    }
}
