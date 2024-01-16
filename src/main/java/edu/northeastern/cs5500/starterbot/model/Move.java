package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/** Holds information of the move */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Move implements Model {
    @Nonnull ObjectId id;
    @Nonnull Integer moveId;
    @Nonnull String name;
    @Nonnull Integer accuracy;
    @Nullable Integer power;
    @Nonnull Integer pp;
    @Nonnull PokemonType type;
    @Nonnull MoveCategory category;

    public static class MoveBuilder {
        public Move build() {
            if (id == null) {
                id = new ObjectId();
            }
            if (moveId == null) {
                throw new IllegalArgumentException("moveId must not be null");
            }
            if (name == null) {
                throw new IllegalArgumentException("name must not be null");
            }
            if (accuracy == null) {
                throw new IllegalArgumentException("accuracy must not be null");
            }
            if (pp == null) {
                throw new IllegalArgumentException("pp must not be null");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must not be null");
            }
            if (category == null) {
                throw new IllegalArgumentException("category must not be null");
            }
            return new Move(id, moveId, name, accuracy, power, pp, type, category);
        }
    }
}
