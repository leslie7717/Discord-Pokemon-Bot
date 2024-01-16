package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;

/** Types of move categories */
public enum MoveCategory {
    PHYSICAL("Physical"),
    SPECIAL("Special");

    @Nonnull String name;

    MoveCategory(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public static MoveCategory fromString(String text) {
        for (MoveCategory moveCategory : MoveCategory.values()) {
            if (moveCategory.name.equalsIgnoreCase(text)) {
                return moveCategory;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
