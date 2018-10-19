/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import graphics.ShadowMap;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class DirectionalLight {
    private Vector3f position;
    private Vector3f color;
    private Vector3f direction;
    private float intensity;
    // Shadows related
    private ShadowMap staticShadowMap, dynamicShadowMap;
    private Matrix4f ortho;
    private Matrix4f lightSpaceMatrix;
    private Vector2f plane;
    private boolean shadowEnable;

    public DirectionalLight(Vector3f position, Vector3f color, Vector3f direction, float intensity, Vector2f plane, boolean shadowEnable) {
        try {
            staticShadowMap = new ShadowMap(1024);
            dynamicShadowMap = new ShadowMap(1024);
            staticShadowMap.initShadowMap();
            dynamicShadowMap.initShadowMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.position = position;
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
        this.plane = plane;
        this.shadowEnable = shadowEnable;
        setOrthoProjection(-10.0f, 10.0f, -10.0f, 10.0f, plane);
    }

    public DirectionalLight(Vector3f position, Vector3f color, Vector3f direction, float intensity, Vector2f plane, int resolution) {
        this(position, color, direction, intensity, plane, true);
        try {
            staticShadowMap.cleanup();
            dynamicShadowMap.cleanup();
            staticShadowMap = new ShadowMap(resolution);
            dynamicShadowMap = new ShadowMap(resolution);
            dynamicShadowMap.initShadowMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setToDynamicOnly() {
        staticShadowMap.cleanup();
        staticShadowMap = null;
    }

    public boolean isDynamicOnly() {
        if (staticShadowMap == null) return true;
        else return false;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;

        float lightAngleX = (float)Math.toDegrees(Math.acos(direction.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(direction.x));
        Matrix4f lightView = new Matrix4f().identity();
        lightView.rotate((float)Math.toRadians(lightAngleX), new Vector3f(1,0 ,0))
                .rotate((float)Math.toRadians(lightAngleY), new Vector3f(0, 1, 0));
        lightView.translate(-position.x, -position.y, -position.z);

        lightSpaceMatrix = new Matrix4f(ortho).mul(lightView);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;

        float lightAngleX = (float)Math.toDegrees(Math.acos(direction.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(direction.x));
        Matrix4f lightView = new Matrix4f().identity();
        lightView.rotate((float)Math.toRadians(lightAngleX), new Vector3f(1,0 ,0))
                .rotate((float)Math.toRadians(lightAngleY), new Vector3f(0, 1, 0));
        lightView.translate(-position.x, -position.y, -position.z);

        lightSpaceMatrix = new Matrix4f(ortho).mul(lightView);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setOrthoProjection(float left, float right, float bottom, float top, Vector2f plane) {
        this.plane = plane;
        ortho = new Matrix4f().setOrtho(left, right, bottom, top, plane.x, plane.y);

        /*Matrix4f lightView = new Matrix4f().identity();
        lightView.rotate(())*/
        float lightAngleX = (float)Math.toDegrees(Math.acos(direction.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(direction.x));
        Matrix4f lightView = new Matrix4f().identity();
        lightView.rotate((float)Math.toRadians(lightAngleX), new Vector3f(1,0 ,0))
                 .rotate((float)Math.toRadians(lightAngleY), new Vector3f(0, 1, 0));
        lightView.translate(-position.x, -position.y, -position.z);

        lightSpaceMatrix = new Matrix4f(ortho).mul(lightView);
    }

    public Matrix4f getLightSpaceMatrix() {
        return lightSpaceMatrix;
    }

    public ShadowMap getStaticShadowMap() {
        return staticShadowMap;
    }

    public ShadowMap getDynamicShadowMap() {
        return dynamicShadowMap;
    }

    public Vector2f getPlane() {
        return plane;
    }

    public boolean isShadowEnabled() {
        return shadowEnable;
    }

    public void setShadowEnable(boolean shadowEnable) {
        this.shadowEnable = shadowEnable;
    }

    /**
     * Cleanup memory
     */
    public void cleanup() {
        if (isDynamicOnly()) {
            dynamicShadowMap.cleanup();
        } else {
            staticShadowMap.cleanup();
            dynamicShadowMap.cleanup();
        }
    }
}