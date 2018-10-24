package game;

import game.level.validation.HdrTestLevel;
import game.level.validation.LightValidation;

import java.util.ArrayList;
import java.util.Arrays;

public class LevelControllerValidation extends LevelController {

    public LevelControllerValidation() {
        super();

        levels = new ArrayList<>(Arrays.asList(
                new LightValidation(this),
                new HdrTestLevel(this)
        ));
    }
}
