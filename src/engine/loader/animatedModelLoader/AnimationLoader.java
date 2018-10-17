package engine.loader.animatedModelLoader;

import engine.animation.JointTransform;
import engine.animation.ModelAnimation;
import engine.animation.Quaternion;
import engine.animation.keyframe.ModelKeyFrame;
import engine.loader.animatedModelLoader.colladaLoader.ColladaLoader;
import engine.loader.animatedModelLoader.dataStructures.AnimationData;
import engine.loader.animatedModelLoader.dataStructures.JointTransformData;
import engine.loader.animatedModelLoader.dataStructures.KeyFrameData;
import engine.util.MyFile;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * This class loads up an animation collada file, gets the information from it,
 * and then creates and returns an {@link ModelAnimation} from the extracted data.
 *
 * @author Karl
 *
 */
public class AnimationLoader {

    /**
     * Loads up a collada animation file, and returns and animation created from
     * the extracted animation data from the file.
     *
     * @param colladaFile
     *            - the collada file containing data about the desired
     *            animation.
     * @return The animation made from the data in the file.
     */
    public static ModelAnimation loadAnimation(String colladaFile) {
        AnimationData animationData = ColladaLoader.loadColladaAnimation(colladaFile);
        ModelKeyFrame[] frames = new ModelKeyFrame[animationData.keyFrames.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = createKeyFrame(animationData.keyFrames[i]);
        }
        return new ModelAnimation(animationData.lengthSeconds, frames);
    }

    /**
     * Creates a keyframe from the data extracted from the collada file.
     *
     * @param data
     *            - the data about the keyframe that was extracted from the
     *            collada file.
     * @return The keyframe.
     */
    private static ModelKeyFrame createKeyFrame(KeyFrameData data) {
        Map<String, JointTransform> map = new HashMap<String, JointTransform>();
        for (JointTransformData jointData : data.jointTransforms) {
            JointTransform jointTransform = createTransform(jointData);
            map.put(jointData.jointNameId, jointTransform);
        }
        return new ModelKeyFrame(data.time, map);
    }

    /**
     * Creates a joint transform from the data extracted from the collada file.
     *
     * @param data
     *            - the data from the collada file.
     * @return The joint transform.
     */
    private static JointTransform createTransform(JointTransformData data) {
        Matrix4f mat = data.jointLocalTransform;
        Vector3f translation = new Vector3f(mat.m30(), mat.m31(), mat.m32());
        Quaternion rotation = Quaternion.fromMatrix(mat);
        return new JointTransform(translation, rotation);
    }

}