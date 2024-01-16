package edu.northeastern.cs5500.starterbot.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public enum PokemonType {
    FIRE("Fire", "🔥"),
    WATER("Water", "💧"),
    GRASS("Grass", "🌱"),
    NORMAL("Normal", "🌟"),
    ELECTRIC("Electric", "⚡"),
    ICE("Ice", "❄️"),
    FIGHTING("Fighting", "🥋"),
    POISON("Poison", "☠️"),
    GROUND("Ground", "🏜️"),
    FLYING("Flying", "🕊️"),
    PSYCHIC("Psychic", "🔮"),
    BUG("Bug", "🐞"),
    ROCK("Rock", "🪨"),
    GHOST("Ghost", "👻"),
    DRAGON("Dragon", "🐉"),
    DARK("Dark", "🌑"),
    STEEL("Steel", "🛡️"),
    FAIRY("Fairy", "🧚");

    @Nonnull String name;

    @Nonnull String emoji;

    PokemonType(@Nonnull String name, @Nonnull String emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    @Nonnull
    public String getEmoji() {
        return emoji;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public static List<PokemonType> getList() {
        List<PokemonType> types = new ArrayList<>();
        return types;
    }

    public static PokemonType fromString(String text) {
        for (PokemonType pokemonType : PokemonType.values()) {
            if (pokemonType.name.equalsIgnoreCase(text)) {
                return pokemonType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    public static String getNamewithEmoji(PokemonType type) {
        return type.getEmoji() + " " + type.getName();
    }
}
