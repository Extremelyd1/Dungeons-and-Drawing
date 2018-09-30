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

public class SpotLight {
    // Same as point light
    private Vector3f color;
    private Vector3f position;
    private float intensity;
    private PointLight.Attenuation attenuation;
    private ShadowMap shadowMap;    // Different Type of shadow map
    private Vector2f plane;
    // SpotLight specific paramters
    private Vector3f coneDirection;
    private float cutOff, outerCutOff;
    private Matrix4f lightSpaceMatrix;

    public SpotLight(Vector3f color, Vector3f position, float intensity, Vector3f coneDirection, float cutOffAngle, float outerCutOffAngle, Vector2f plane) {
        try {
            shadowMap = new ShadowMap(2048);
            shadowMap.initShadowMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        attenuation = new PointLight.Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;

        this.coneDirection = coneDirection;
        this.cutOff = cutOffAngle;
        this.outerCutOff = outerCutOffAngle;
        setPlane(plane);
    }

    public SpotLight(Vector3f color, Vector3f position, float intensity, Vector3f coneDirection, float cutOffAngle, float outerCutOff,
                     PointLight.Attenuation attenuation, Vector2f plane) {
        this(color, position, intensity, coneDirection, cutOffAngle, outerCutOff, plane);
        this.attenuation = attenuation;
    }

    private SpotLight(Vector3f color, Vector3f position, float intensity, Vector3f coneDirection, float cutOffAngle, float outerCutOff,
                      PointLight.Attenuation attenuation, ShadowMap shadowMap, Vector2f plane) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.attenuation = attenuation;
        this.shadowMap = shadowMap;

        this.coneDirection = coneDirection;
        this.cutOff = cutOffAngle;
        this.outerCutOff = outerCutOff;
        this.setPlane(plane);
    }


    public SpotLight(SpotLight spotLight) {
        this(new Vector3f(spotLight.getColor()), new Vector3f(spotLight.getPosition()), spotLight.getIntensity(),
                spotLight.getConeDirection(), spotLight.getCutOff(), spotLight.getOuterCutOff(), spotLight.getAttenuation(),
                spotLight.getShadowMap(), spotLight.getPlane());
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

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public PointLight.Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(PointLight.Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public float getOuterCutOff() {
        return outerCutOff;
    }

    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = outerCutOff;
    }

    public Matrix4f getLightSpaceMatrix(){
        return lightSpaceMatrix;
    }

    public Vector2f getPlane() {
        return plane;
    }

    public void setPlane(Vector2f plane) {
        this.plane = plane;
        Matrix4f projection = new Matrix4f();
        projection.setPerspective((float)Math.toRadians(90.0f), 1.0f, plane.x, plane.y);

        Matrix4f lightViewMatrix = new Matrix4f();
        lightViewMatrix.lookAt(
                getPosition(),
                new Vector3f(getPosition()).add(getConeDirection()),
                new Vector3f(0.0f, 1.0f, 0.0f));

        lightSpaceMatrix = projection.mul(lightViewMatrix);
    }
}