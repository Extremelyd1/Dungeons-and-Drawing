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
        ModelKeyFrame[] frames = new ModelKeyFrame[9];
        for (int i = 0; i < frames.length; i++) {
            Map<String, JointTransform> map = new HashMap<>();
            for (Joint joint : jointList) {
                map.put(joint.name, createCustomTransform(i, joint.name));
            }
            frames[i] = new ModelKeyFrame(i * 4, map);
        }
        Map<String, JointTransform> map = new HashMap<>();
        for (Joint joint : jointList) {
            map.put(joint.name, createCustomTransform(-1, joint.name));
        }
        ModelKeyFrame idleKeyFrame = new ModelKeyFrame(0f, map);

        return new ModelAnimation((frames.length - 1) * 4, frames, idleKeyFrame);
    }
    
    private static JointTransform createCustomTransform(int keyFrame, String jointName) {

        if (keyFrame == -1) {

            // Idle
            if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            }

            else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -45);
            } else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -45);
            }

        } else if (keyFrame == 0 || keyFrame == 8) {

            // Passing
            if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 30);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 90);
            } else if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            }
            
            else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, -30);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -90);
            } else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, 0);
            }
            
        } else if (keyFrame == 1) {

            // High-point
            if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 90);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 90);
            } else if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, -20);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            }

            else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, -60);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -90);
            } else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -90);
            }

        } else if (keyFrame == 2) {

            // Contact
            if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, -45);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            }

            else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, -90);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -90);
            } else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -90);
            }

        } else if (keyFrame == 3) {

            // Recoil
            if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 90);
            }

            else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, -60);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -80);
            } else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, 30);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -90);
            }

        } else if (keyFrame == 4) {

            // Passing
            if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 30);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 90);
            } else if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            }

            else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, -30);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -90);
            } else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, 0);
            }

        } else if (keyFrame == 5) {

            // High-point
            if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 90);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 90);
            } else if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, -20);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            }

            else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, -60);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -90);
            } else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -90);
            }

        } else if (keyFrame == 6) {

            // Contact
            if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, -45);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            }

            else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, -90);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -90);
            } else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -90);
            }

        } else if (keyFrame == 7) {

            // Recoil
            if (jointName.equals("Upper_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Lower_Leg_L")) {
                return createTransform(Quaternion.Axis.X, 45);
            } else if (jointName.equals("Upper_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 0);
            } else if (jointName.equals("Lower_Leg_R")) {
                return createTransform(Quaternion.Axis.X, 90);
            }

            else if (jointName.equals("Upper_Arm_L")) {
                return createTransform(Quaternion.Axis.X, -60);
            } else if (jointName.equals("Lower_Arm_L")) {
                return createTransform(Quaternion.Axis.Z, -80);
            } else if (jointName.equals("Upper_Arm_R")) {
                return createTransform(Quaternion.Axis.X, 30);
            } else if (jointName.equals("Lower_Arm_R")) {
                return createTransform(Quaternion.Axis.Z, -90);
            }

        }
        
        return createTransform(Quaternion.Axis.X, 0);
    }
    
    private static JointTransform createTransform(Quaternion.Axis axis, float degrees) {
        return new JointTransform(new Quaternion(axis, degrees));
    }

    private static List<Joint> createJointsList(Joint root, List<Joint> currentList) {
        currentList.add(root);
        for (Joint child : root.children) {
            currentList = createJointsList(child, currentList);
        }
        return currentList;
    }
}