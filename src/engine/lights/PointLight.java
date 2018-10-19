/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import engine.Transformation;
import graphics.ShadowMap;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PointLight {
    private Vector3f color;
    private Vector3f position;
    private float intensity;
    private Attenuation attenuation;
    private ShadowMap staticShadowMap, dynamicShadowMap;
    private Vector2f plane;
    private Matrix4f[] views = new Matrix4f[6];

    public PointLight(Vector3f color, Vector3f position, float intensity, Vector2f plane) {
        try {
            staticShadowMap = new ShadowMap(2048);
            dynamicShadowMap = new ShadowMap(1024);
            staticShadowMap.initShadowCubeMap();
            dynamicShadowMap.initShadowCubeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.plane = plane;

        setupShadowViews();
    }

    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation, Vector2f plane) {
        this(color, position, intensity, plane);
        this.attenuation = attenuation;
    }

    public Matrix4f getView(int i) {
        return views[i];
    }

    public void setupShadowViews() {
        Transformation transformation = new Transformation();
        Matrix4f shadowProj = new Matrix4f();
        shadowProj.setPerspective((float) Math.toRadians(90), 1.0f,
                plane.x,
                plane.y);
        views[0] = transformation.getProjectionWithDirection(
                position,
                new Vector3f(1.0f, 0.0f, 0.0f),
                shadowProj,
                new Vector3f(0.0f, -1.0f, 0.0f));
        views[1] = transformation.getProjectionWithDirection(
                position,
                new Vector3f(-1.0f, 0.0f, 0.0f),
                shadowProj,
                new Vector3f(0.0f, -1.0f, 0.0f));
        views[2] = transformation.getProjectionWithDirection(
                position,
                new Vector3f(0.0f, 1.0f, 0.0f),
                shadowProj,
                new Vector3f(0.0f, 0.0f, 1.0f));
        views[3] = transformation.getProjectionWithDirection(
                position,
                new Vector3f(0.0f, -1.0f, 0.0f),
                shadowProj,
                new Vector3f(0.0f, 0.0f, -1.0f));
        views[4] = transformation.getProjectionWithDirection(
                position,
                new Vector3f(0.0f, 0.0f, 1.0f),
                shadowProj,
                new Vector3f(0.0f, -1.0f, 0.0f));
        views[5] = transformation.getProjectionWithDirection(
                position,
                new Vector3f(0.0f, 0.0f, -1.0f),
                shadowProj,
                new Vector3f(0.0f, -1.0f, 0.0f));
    }

    public void setToDynamicOnly() {
        staticShadowMap.cleanup();
        staticShadowMap = dynamicShadowMap;
    }

    public boolean isDynamicOnly() {
        if (staticShadowMap == dynamicShadowMap) return true;
        else return false;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        setupShadowViews();
    }

    public float getIntensity() {
        return intensity;
    }

    public ShadowMap getStaticShadowMap() {
        return staticShadowMap;
    }

    public ShadowMap getDynamicShadowMap() {
        return dynamicShadowMap;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    public static class Attenuation {

        private float constant;

        private float linear;

        private float exponent;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {
            return constant;
        }

        public void setConstant(float constant) {
            this.constant = constant;
        }

        public float getLinear() {
            return linear;
        }

        public void setLinear(float linear) {
            this.linear = linear;
        }

        public float getExponent() {
            return exponent;
        }

        public void setExponent(float exponent) {
            this.exponent = exponent;
        }
    }

    public Vector2f getPlane() {
        return plane;
    }

    public void setPlane(Vector2f plane) {
        this.plane = plane;
    }

    /**
     * Cleanup memory
     */
    public void cleanup() {
        staticShadowMap.cleanup();
        dynamicShadowMap.cleanup();
    }
}