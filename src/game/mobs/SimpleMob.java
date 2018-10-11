package game.mobs;

import engine.entities.Entity;
import engine.entities.LivingEntity;
import engine.util.Spline;
import game.map.Map;
import game.map.tile.Tile;
import graphics.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import pathfinding.A_star;
import sun.security.ssl.Debug;

import java.util.List;

public class SimpleMob extends LivingEntity {
    private A_star pathfinder = new A_star();
    private Tile currentTile, targetTile;
    private Vector2f lastTargetPos;
    private Vector3f direction = new Vector3f(0, 0, 1);
    private List<Tile> path;
    private int pathProgress = 0;
    private Entity target;
    private Spline pathSmoother = new Spline();
    private boolean isInLineOfSight = false;
    private boolean followOnSightOnly = true;

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

    public void followOnSightOnly(boolean sightOnly){
        followOnSightOnly = sightOnly;
    }

    /**
     * Custom line of sight algorithm
     *
     * @param start
     * @param end
     * @return
     */
    public boolean isInLineOfSightWithoutCollision(Vector2f start, Vector2f end, float radius, float precision){
        Vector2f pos = new Vector2f(start);
        Vector2f dir = new Vector2f(end).sub(start).normalize();
        float length = (new Vector2f(end).sub(start)).length();
        Vector2f perpDir = new Vector2f(dir.y, -dir.x);
        Vector2f spot1, spot2;
        float t = 0;

        while (t < 1.0f) {
            spot1 = new Vector2f(pos).add(new Vector2f(perpDir).mul(radius));
            spot2 = new Vector2f(pos).add(new Vector2f(perpDir).mul(-radius));

            try {
                if (isWithinMap(pos.x, pos.y) && getMap().getTile(Math.round(pos.x), Math.round(pos.y)).isSolid()) {
                    return false;
                } else if (isWithinMap(spot2.x, spot2.y) && getMap().getTile(Math.round(spot2.x), Math.round(spot2.y)).isSolid()) {
                    return false;
                } else if (isWithinMap(spot1.x, spot1.y) && getMap().getTile(Math.round(spot1.x), Math.round(spot1.y)).isSolid()) {
                    return false;
                }
            } catch (Exception e) {
                Debug.println("MOB", "EXCEPTION");
            }

            pos = new Vector2f(start).mul(1.0f - t);
            pos.add(new Vector2f(end).mul(t));

            t += precision / length;
        }

        return true;
    }
    public boolean isInLineOfSight(Vector2f start, Vector2f end, float precision){
        Vector2f pos = new Vector2f(start);
        float length = (new Vector2f(end).sub(start)).length();
        float t = 0;

        while (t < 1.0f) {
            try {
                if (isWithinMap(pos.x, pos.y) && getMap().getTile(Math.round(pos.x), Math.round(pos.y)).isSolid()) {
                    return false;
                }
            } catch (Exception e) {
                Debug.println("MOB", "EXCEPTION2");
            }

            pos = new Vector2f(start).mul(1.0f - t);
            pos.add(new Vector2f(end).mul(t));

            t += precision / length;
        }

        return true;
    }

    private boolean isWithinMap(float x, float y){
        int xr = Math.round(x), yr = Math.round(y);
        if (0 <= xr && xr < getMap().getWidth() && 0 <= yr && yr < getMap().getHeight()) {
            return true;
        }

        return false;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (target != null) {
            // Variables
            Vector2f pos = new Vector2f(position.x, position.z);
            Vector2f curTarPos = new Vector2f(target.getPosition().x, target.getPosition().z);
            if (followOnSightOnly && isInLineOfSight(pos, curTarPos, delta * getSpeed())) {
                lastTargetPos = curTarPos;
            } else if (!followOnSightOnly) {
                lastTargetPos = curTarPos;
            }
            // Check if mob can see the target
            if ((new Vector2f(lastTargetPos).sub(pos)).length() > 0.5f) {
                if (isInLineOfSightWithoutCollision(pos, lastTargetPos, 0.35f, delta * getSpeed())) {
                    isInLineOfSight = true;
                    setupPathSmootherMode3(position, direction, new Vector3f(lastTargetPos.x, 1, lastTargetPos.y));
                    pathSmoother.update(delta * getSpeed() * 0.8f);
                    Vector3f finalPos = pathSmoother.getResult();
                    direction = new Vector3f(finalPos).sub(position).normalize();
                    setRotation(0, (float) Math.toDegrees(-Math.atan2(direction.z, direction.x)), 0);
                    setPosition(finalPos);
                } else {
                    currentTile = super.getMap().getTile(Math.round(pos.x), Math.round(pos.y));
                    Tile newTargetCurrentTile = super.getMap().getTile(Math.round(lastTargetPos.x), Math.round(lastTargetPos.y));
                    if (newTargetCurrentTile != targetTile || isInLineOfSight) {
                        targetTile = newTargetCurrentTile;
                        path = findPathToTile(currentTile, targetTile);
                        pathProgress = 1;
                        if (!isInLineOfSight) {
                            setupPathSmootherMode2(
                                    getPosition(),
                                    new Vector3f(path.get(pathProgress - 1).getPosition().x, getPosition().y, path.get(pathProgress - 1).getPosition().y),
                                    new Vector3f(path.get(pathProgress).getPosition().x, getPosition().y, path.get(pathProgress).getPosition().y));
                        } else {
                            setupPathSmootherMode2(
                                    getPosition(),
                                    new Vector3f(getPosition()).add(new Vector3f(direction).normalize().mul(0.2f)),
                                    new Vector3f(path.get(pathProgress).getPosition().x, getPosition().y, path.get(pathProgress).getPosition().y));
                        }
                    }
                    float remaining = pathSmoother.update(delta * getSpeed());
                    while (remaining != 0 && pathProgress < path.size() - 1) {
                        setupPathSmootherMode1(
                                new Vector3f(path.get(pathProgress - 1).getPosition().x, getPosition().y, path.get(pathProgress - 1).getPosition().y),
                                new Vector3f(path.get(pathProgress).getPosition().x, getPosition().y, path.get(pathProgress).getPosition().y),
                                new Vector3f(path.get(pathProgress + 1).getPosition().x, getPosition().y, path.get(pathProgress + 1).getPosition().y));
                        pathProgress += 1;
                        remaining = pathSmoother.update(remaining);
                    }
                    isInLineOfSight = false;

                    Vector3f finalPos = pathSmoother.getResult();
                    direction = new Vector3f(finalPos).sub(position).normalize();
                    setRotation(0, (float) Math.toDegrees(-Math.atan2(direction.z, direction.x)), 0);
                    setPosition(finalPos);
                }
            }
        }
    }

    private void setupPathSmootherMode3(Vector3f currentPos, Vector3f direction, Vector3f targetPos){
        Vector3f entrancePoint, midPoint, leavingPoint;

        entrancePoint = new Vector3f(currentPos);
        leavingPoint = new Vector3f(targetPos);
        midPoint = new Vector3f(entrancePoint).add(new Vector3f(direction).normalize().mul(0.2f));

        pathSmoother.setup(entrancePoint, midPoint, leavingPoint);
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

    private List<Tile> findPathToTile(Tile start, Tile target) {
        return pathfinder.computePath(start, target, super.getMap());
    }
}
