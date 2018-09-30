/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import graphics.ShadowMap;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PointLight {
    private Vector3f color;
    private Vector3f position;
    private float intensity;
    private Attenuation attenuation;
    private ShadowMap shadowMap;
    private Vector2f plane;
    
    public PointLight(Vector3f color, Vector3f position, float intensity, Vector2f plane) {
        try {
            shadowMap = new ShadowMap(1024);
            shadowMap.initShadowCubeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.plane = plane;
    }

    public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation, Vector2f plane) {
        this(color, position, intensity, plane);
        this.attenuation = attenuation;
    }

    private PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation, ShadowMap shadowMap, Vector2f plane) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.attenuation = attenuation;
        this.shadowMap = shadowMap;
        this.plane = plane;
    }

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()),
                pointLight.getIntensity(), pointLight.getAttenuation(), pointLight.getShadowMap(), pointLight.getPlane());
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
    }

    public float getIntensity() {
        return intensity;
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
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
}