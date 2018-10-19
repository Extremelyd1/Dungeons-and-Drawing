package engine.lights;

import java.util.ArrayList;
import java.util.List;

/**
 * Record type to store each individual light
 */
public class SceneLight {

    public List<PointLight> pointLights;
    public List<SpotLight> spotLights;
    public AmbientLight ambientLight;
    public DirectionalLight directionalLight;

    public SceneLight() {
        this.pointLights = new ArrayList<>();
        this.spotLights = new ArrayList<>();
        this.ambientLight = new AmbientLight();
        this.directionalLight = null;
    }

    /**
     * Cleanup all the lights
     */
    public void cleanup() {
        pointLights.forEach(PointLight::cleanup);
        spotLights.forEach(SpotLight::cleanup);
        directionalLight.cleanup();
    }
}
