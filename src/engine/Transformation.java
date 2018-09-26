package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * This class deals with transformations. The following matrices are of importance:
 * <ul>
 * <li> Projection Matrix </li>
 * <li> Translation Matrix </li>
 * <li> Rotation Matrix </li>
 * <li> Scale Matrix </li>
 * <li> World Matrix </li>
 * </ul>
 *
 * <b> Projection Matrix: .perspective() </b>
 * <p>
 * --                                                                    --
 * | (1/tan(fov/2))/a   0               0           0                       |
 * | 0                  1/tan(fov/2)    0           0                       |
 * | 0                  0               -zp/zm      (-2*z_far*z_near)/zm    |
 * | 0                  0               -1          0                       |
 * --                                                                    --
 * <p>
 * With:
 * <ul>
 * <li> a = aspect ratio = window width / window height </li>
 * <li> z_far = furtherst point the camera can see </li>
 * <li> z_near = closest point the camera can see </li>
 * <li> zm = z_far - z_near </li>
 * <li> zp = z_far + z_near </li>
 * <li> fov = field of view (in radians) </li>
 * </ul>
 *
 * <b> Translation Matrix: .translate() </b>
 * --               --
 * | 1    0    0    dx |
 * | 0    1    0    dy |
 * | 0    0    0    dz |
 * | 0    0    1    1  |
 * --               --
 *
 * <b> Scale Matrix: .scale() </b>
 * --               --
 * | sx   0    0    0 |
 * | 0    sy   0    0 |
 * | 0    0    sz   0 |
 * | 0    0    0    1 |
 * --               --
 *
 * <b> Rotation Matrix </b>
 * The rotation matrix is unique depending on which axis you are rotating around.
 * The final matrix is computed by multiplying these matrices together.
 *
 * <b> World Matrix </b>
 * The world matrix can be found by multiplying the identity matrix with first
 * the translation matrix, then the different rotation matrices and then the
 * scale matrix.
 *
 * @author Cas Wognum (TU/e, 1012585)
 */
public class Transformation {
    private final Matrix4f worldMatrix;

    /**
     * Apply camera transformations (field of view, aspect ratio, etc.)
     */
    private final Matrix4f projectionMatrix;

    /**
     * Combines world and view coordinates
     */
    private final Matrix4f modelViewMatrix;

    /**
     * Simulates camera movement
     */
    private final Matrix4f viewMatrix;

    /**
     * Required for Shadows
     */
    private final Matrix4f lightViewMatrix;
    private final Matrix4f modelLightMatrix;
    private final Matrix4f modelLightViewMatrix;
    private final Matrix4f orthoProjMatrix;

    public Transformation() {
        worldMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();

        lightViewMatrix = new Matrix4f();
        modelLightMatrix = new Matrix4f();
        modelLightViewMatrix = new Matrix4f();
        orthoProjMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
        worldMatrix.identity().translate(offset).
                rotateX((float) Math.toRadians(rotation.x)).
                rotateY((float) Math.toRadians(rotation.y)).
                rotateZ((float) Math.toRadians(rotation.z)).
                scale(scale);
        return worldMatrix;
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(GameEntity entity, Matrix4f viewMatrix) {
        Vector3f rotation = entity.getRotation();
        modelViewMatrix.identity().translate(entity.getPosition()).
                rotateX((float) Math.toRadians(-rotation.x)).
                rotateY((float) Math.toRadians(-rotation.y)).
                rotateZ((float) Math.toRadians(-rotation.z)).
                scale(entity.getScale());
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    /**
     * lightViewMatrix
     */
    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public void setLightViewMatrix(Matrix4f lightViewMatrix) {
        this.lightViewMatrix.set(lightViewMatrix);
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // Rotation
        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
        orthoProjMatrix.identity();
        orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
        return orthoProjMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix(){
        return orthoProjMatrix;
    }

    public Matrix4f updateModelLightViewMatrix(GameEntity gameItem, Matrix4f matrix) {
        Vector3f rotation = gameItem.getRotation();
        modelLightMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        modelLightViewMatrix.set(matrix);
        return modelLightViewMatrix.mul(modelLightMatrix);
    }
}
