package edu.northeastern.cs5500.starterbot.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Pokemon implements Model {
    @Nonnull @Builder.Default ObjectId id = new ObjectId();

    @Nonnull Integer pokedexNumber;

    @Nonnull @Builder.Default Integer level = 5;

    @Nonnull @Nonnegative Integer currentHp;
    @Nonnull Integer hp;

    @Nonnull @Builder.Default Integer xp = 0;

    @Nonnull Integer attack;
    @Nonnull Integer defense;
    @Nonnull Integer specialAttack;
    @Nonnull Integer specialDefense;
    @Nonnull Integer speed;
    @Nonnull @Builder.Default Boolean catchAttempted = false;

    @Singular @Nonnull List<Move> moves;

    public Pokemon(
            Integer pokedexNumber,
            Integer currentHp,
            Integer hp,
            Integer xp,
            Integer attack,
            Integer defense,
            Integer specialAttack,
            Integer specialDefense,
            Integer speed,
            Boolean catchAttempted,
            List<Move> moves) {
        if (pokedexNumber == null) {
            throw new IllegalArgumentException("pokedexNumber must not be null");
        }
        if (currentHp == null || currentHp < 0) {
            throw new IllegalArgumentException("currentHp must be non-null and non-negative");
        }
        if (hp == null) {
            throw new IllegalArgumentException("hp must not be null");
        }
        if (xp == null) {
            xp = 0;
        }
        if (attack == null) {
            throw new IllegalArgumentException("attack must not be null");
        }
        if (defense == null) {
            throw new IllegalArgumentException("defense must not be null");
        }
        if (specialAttack == null) {
            throw new IllegalArgumentException("specialAttack must not be null");
        }
        if (specialDefense == null) {
            throw new IllegalArgumentException("specialDefense must not be null");
        }
        if (speed == null) {
            throw new IllegalArgumentException("speed must not be null");
        }
        if (moves == null) {
            moves = new ArrayList<>();
            if (catchAttempted == null) {
                catchAttempted = false;
            }

            this.id = new ObjectId();
            this.pokedexNumber = pokedexNumber;
            this.currentHp = currentHp;
            this.hp = hp;
            this.xp = xp;
            this.attack = attack;
            this.defense = defense;
            this.specialAttack = specialAttack;
            this.specialDefense = specialDefense;
            this.speed = speed;
            this.level = 5;
            this.moves = moves;
            this.catchAttempted = catchAttempted;
        }
    }
}
