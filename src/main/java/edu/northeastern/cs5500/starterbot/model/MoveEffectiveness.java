package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nullable;
import lombok.Getter;

public enum MoveEffectiveness {
    NO_EFFECT("It has no effect!", 0),
    QUARTER_EFFECT("It's not very effective...", 0.25),
    HALF_EFFECT("It's not very effective...", 0.5),
    FULL_EFFECT(null, 1),
    DOUBLE_EFFECT("It's super effective!", 2),
    QUAD_EFFECT("It's super effective!", 4);

    @Nullable @Getter String text;

    @Getter double effectiveness;

    MoveEffectiveness(String text, double effectiveness) {
        this.text = text;
        this.effectiveness = effectiveness;
    }

    public static MoveEffectiveness getEffectiveness(double effect) {
        switch (String.valueOf(effect)) {
            case "0.0":
                return MoveEffectiveness.NO_EFFECT;
            case "0.25":
                return MoveEffectiveness.QUARTER_EFFECT;
            case "0.5":
                return MoveEffectiveness.HALF_EFFECT;
            case "1.0":
                return MoveEffectiveness.FULL_EFFECT;
            case "2.0":
                return MoveEffectiveness.DOUBLE_EFFECT;
            case "4.0":
                return MoveEffectiveness.QUAD_EFFECT;
        }
        throw new IllegalStateException();
    }
}
