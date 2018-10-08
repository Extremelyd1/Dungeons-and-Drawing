package engine.animation;

import engine.animation.keyframe.KeyFrame;

public class SimpleAnimationLoader implements AnimationLoader {

    public Animation load() {
        KeyFrame[] keyframes = new KeyFrame[2];
        keyframes[0] = new KeyFrame(0, 0);
        keyframes[1] = new KeyFrame(10, 90);

        return new Animation(10, keyframes, false);
    }

}
