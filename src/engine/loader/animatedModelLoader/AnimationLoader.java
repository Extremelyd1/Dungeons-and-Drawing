package engine.loader.animatedModelLoader;

import engine.animation.JointTransform;
import engine.animation.ModelAnimation;
import engine.animation.Quaternion;
import engine.animation.keyframe.ModelKeyFrame;
import engine.entities.animatedModel.AnimatedModel;
import engine.entities.animatedModel.Joint;
import engine.loader.animatedModelLoader.colladaLoader.ColladaLoader;
import engine.loader.animatedModelLoader.dataStructures.AnimationData;
import engine.loader.animatedModelLoader.dataStructures.JointTransformData;
import engine.loader.animatedModelLoader.dataStructures.KeyFrameData;
import engine.util.MyFile;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @return The animation made from the data in the file.
     */
    public static ModelAnimation loadAnimation(AnimatedModel model) {
        List<Joint> jointList = createJointsList(model.getRootJoint(), new ArrayList<>());
        ModelKeyFrame[] frames = new ModelKeyFrame[3];
        for (int i = 0; i < 3; i++) {
            Map<String, JointTransform> map = new HashMap<>();
            for (Joint joint : jointList) {
                map.put(joint.name, createCustomTransform(joint.name));
            }
            frames[i] = new ModelKeyFrame(i, map);
        }
        return new ModelAnimation(2f, frames);
    }

    private static List<Joint> createJointsList(Joint root, List<Joint> currentList) {
        currentList.add(root);
        for (Joint child : root.children) {
            currentList = createJointsList(child, currentList);
        }
        return currentList;
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
        Quaternion rotation = Quaternion.fromMatrix(mat);
        return new JointTransform(rotation);
    }

    private static boolean firstReturn = false;

    private static JointTransform createCustomTransform(String jointName) {
        if (jointName.equals("Upper_Arm_R")) {
            Quaternion rotation = new Quaternion((float) Math.sin(Math.PI / 4), 0, 0, (float) Math.cos(Math.PI / 4));

            return new JointTransform(rotation);
        } else if (jointName.equals("Lower_Arm_R")) {
            Quaternion rotationUp = new Quaternion((float) Math.sin(Math.PI / 4), 0, 0, (float) Math.cos(Math.PI / 4));
            Quaternion rotationSide;
            if (firstReturn) {
                firstReturn = false;
                rotationSide = new Quaternion(0, 0, (float) Math.sin(Math.PI / 8), (float) Math.cos(Math.PI / 8));
            } else {
                firstReturn = true;
                rotationSide = new Quaternion(0, 0, (float) Math.sin(-Math.PI / 8), (float) Math.cos(-Math.PI / 8));
            }

            Quaternion rotation = Quaternion.fromMatrix(((Matrix4f) rotationUp.toRotationMatrix()).mul(rotationSide.toRotationMatrix()));
            return new JointTransform(rotation);
        } else {
            Quaternion rotation = new Quaternion(0, 0, 0, 1);
            return new JointTransform(rotation);
        }
    }

}