package game.mobs;

import engine.entities.Entity;
import engine.entities.LivingEntity;
import engine.util.Spline;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
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

    /**
     * Xiaolin Wu line algorithm
     *
     * @param start
     * @param end
     * @return
     */
    public boolean isInLineOfSight(Tile start, Tile end){
        int mid;
        Vector2i s = new Vector2i(start.getPosition()), e = new Vector2i(end.getPosition());
        boolean steep = Math.abs(e.y - s.y) > Math.abs(e.x - s.x);
        if (steep) {
            s.x = start.getPosition().y;
            s.y = start.getPosition().x;
            e.x = end.getPosition().y;
            e.y = end.getPosition().x;
        }
        if (s.x > e.x) {
            mid = s.x;
            s.x = e.x;
            e.x = mid;
            mid = s.y;
            s.y = e.y;
            e.y = mid;
        }

        float dx = e.x - s.x;
        float dy = e.y - s.y;
        float gradient = dy / dx;
        float y = s.y + gradient;

        for (int x = s.x + 1; x <= e.x - 1; x++) {
            if (0 <= x && x < getMap().getWidth() && 0 <= (int)y && (int)y < getMap().getHeight()) {
                if (getMap().getTile(x, (int) y).isSolid()) {
                    return false;
                }
            }
            if (0 <= x && x < getMap().getWidth() && 0 <= (int)y + 1 && (int)y + 1 < getMap().getHeight()) {
                if (getMap().getTile(x, (int) y + 1).isSolid()) {
                    return false;
                }
            }
            y += gradient;
        }

        return true;
    }

    private Spline pathSmoother = new Spline();
    private boolean isInLineOfSight = false;

    @Override
    public void update(float delta) {
        super.update(delta);
        // Variables
        Vector3f direction;
        Vector3f finalPos;
        // Get Mob Tile
        currentTile = super.getMap().getTile(Math.round(getPosition().x), Math.round(getPosition().z));
        // Check if there is a target set
        if (target != null) {
            // Get the current Target entity tile
            Tile targetCurrentTile = super.getMap().getTile(Math.round(target.getPosition().x), Math.round(target.getPosition().z));
            // Compare with the stored Target entity tile
            if (targetCurrentTile != targetTile) {
                // Recalculate Path
                targetTile = targetCurrentTile;
                path = findPathToTile(currentTile, targetCurrentTile);
                pathProgress = 1;
                if (pathProgress < path.size() - 1) {
                    setupPathSmootherMode2(
                            getPosition(),
                            new Vector3f(path.get(pathProgress - 1).getPosition().x, getPosition().y, path.get(pathProgress - 1).getPosition().y),
                            new Vector3f(path.get(pathProgress    ).getPosition().x, getPosition().y, path.get(pathProgress    ).getPosition().y));
                }
            }
            // Calculate Movement using Path Smoothing
            float remaining = pathSmoother.update(delta * getSpeed());
            while (remaining != 0 && pathProgress < path.size() - 1) {
                setupPathSmootherMode1(
                        new Vector3f(path.get(pathProgress - 1).getPosition().x, getPosition().y,path.get(pathProgress - 1).getPosition().y),
                        new Vector3f(path.get(pathProgress    ).getPosition().x, getPosition().y,path.get(pathProgress    ).getPosition().y),
                        new Vector3f(path.get(pathProgress + 1).getPosition().x, getPosition().y,path.get(pathProgress + 1).getPosition().y));
                pathProgress += 1;
                remaining = pathSmoother.update(remaining);
            }
            finalPos = pathSmoother.getResult();
            direction = new Vector3f(position).sub(finalPos).normalize();

            // Calculate Movement in the direction of the target
            if (isInLineOfSight(currentTile, targetTile)) {
                Vector3f nextFrameDirection = new Vector3f(target.getPosition()).sub(position).normalize();
                Vector3f nextFramePos;
                if (remaining != 0) {
                    nextFramePos = new Vector3f(nextFrameDirection ).mul(remaining).add(position);
                } else {
                    nextFramePos = new Vector3f(nextFrameDirection ).mul(delta * getSpeed()).add(position);
                }
                // Ensure that there are no collisions
                finalPos = nextFramePos;
                direction = nextFrameDirection;
            }

            if (direction.length() != 0) {
                setRotation(0, (float) Math.toDegrees(-Math.atan2(direction.z, direction.x)), 0);
                setPosition(finalPos);
            }
        }
    }
    private void setupPathSmootherMode2(Vector3f currentPos, Vector3f currentTilePos, Vector3f succesiveTilePos){
        Vector3f entrancePoint, midPoint, leavingPoint;

        entrancePoint = new Vector3f(currentPos);
        leavingPoint = new Vector3f(succesiveTilePos).sub(currentTilePos);
        leavingPoint.mul(0.5f);
        leavingPoint = new Vector3f(currentTilePos).add(leavingPoint);
        midPoint = new Vector3f(leavingPoint).sub(entrancePoint);
        midPoint.mul(0.5f);
        midPoint = new Vector3f(entrancePoint).add(midPoint);

        pathSmoother.setup(entrancePoint, midPoint, leavingPoint);
    }

    private void setupPathSmootherMode1(Vector3f precedingTilePos, Vector3f currentTilePos, Vector3f succesiveTilePos){
        Vector3f entrancePoint, leavingPoint;

        entrancePoint = new Vector3f(currentTilePos).sub(precedingTilePos).normalize();
        entrancePoint.mul(0.5f);
        entrancePoint = new Vector3f(currentTilePos).sub(entrancePoint);
        leavingPoint = new Vector3f(succesiveTilePos).sub(currentTilePos).normalize();
        leavingPoint.mul(0.5f);
        leavingPoint = new Vector3f(currentTilePos).add(leavingPoint);

        pathSmoother.setup(entrancePoint, currentTilePos, leavingPoint);
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
