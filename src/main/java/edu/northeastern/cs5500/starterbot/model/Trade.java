package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade implements Model {
    ObjectId id;

    @Nonnull ObjectId trainerId;
    @Nonnull ObjectId offerPokemon;
    @Nonnull Integer requestPokemonPokedexNumber;

    /**
     * Trade constructor
     *
     * @param trainerId trade initiater's object id
     * @param offPokemon objectId of pokemon being offered
     * @param requestPokemonPokedexNumber pokedex number of pokemon being requested
     */
    public Trade(
            @Nonnull ObjectId trainerId,
            @Nonnull ObjectId offPokemon,
            @Nonnull Integer requestPokemonPokedexNumber) {
        this.trainerId = trainerId;
        this.offerPokemon = offPokemon;
        this.requestPokemonPokedexNumber = requestPokemonPokedexNumber;
    }
}
