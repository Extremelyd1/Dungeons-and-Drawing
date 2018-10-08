package game.mobs;

import engine.entities.Entity;
import engine.entities.LivingEntity;
import engine.util.ColorInterpolator;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector3f;
import pathfinding.A_star;

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

    @Override
    public void update(float delta) {
        super.update(delta);
        // Calculate current mob tile and entity tile
        currentTile = super.getMap().getTile(Math.round(currentTile.getPosition().x), Math.round(currentTile.getPosition().y));
        if (target != null) {
            Tile targetCurrentTile = super.getMap().getTile(Math.round(target.getPosition().x), Math.round(target.getPosition().y));
            // Recalculate Path only if the target tile has changed
            if (targetCurrentTile != targetTile) {
                targetTile = targetCurrentTile;
                if (targetTile != null) {
                    path = findPathToTile(currentTile, targetTile);
                    pathProgress = 0;
                }
            }

            // Calculate direction Vector
            Vector3f direction;

            if (currentTile.getPosition() == path.get(pathProgress).getPosition()) {
                if (pathProgress < path.size() - 3) {
                    pathProgress++;
                    direction = getDirectionVector(path.get(pathProgress), currentTile);
                } else {
                    direction = getDirectionVector(target.getPosition(), this.getPosition());
                }
            } else {
                direction = getDirectionVector(path.get(pathProgress), currentTile);
            }

            // Update Rotation
            setRotation(0, (float)Math.atan2(direction.z, direction.x), 0);
            // Update Position
            setPosition(position.add(direction.mul(delta / getSpeed())));
        }
    }

    private Vector3f getDirectionVector(Tile start, Tile end) {
        return new Vector3f(
                path.get(pathProgress).getPosition().x,
                0,
                path.get(pathProgress).getPosition().y).
                sub(new Vector3f(
                        currentTile.getPosition().x,
                        0,
                        currentTile.getPosition().y))
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
