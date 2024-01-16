package edu.northeastern.cs5500.starterbot.model;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PokemonSpecies {
    // Same pokedexNumber everywhere
    @Nonnull final Integer pokedexNumber;

    @Nonnull final String imageURL;

    @Nonnull final String name;

    @Nonnull final List<PokemonType> types;
}
