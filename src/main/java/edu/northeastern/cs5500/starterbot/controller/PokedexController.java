package edu.northeastern.cs5500.starterbot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies.PokemonSpeciesBuilder;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class PokedexController {

    TrainerController trainerController;
    PokemonController pokemonController;

    @Inject
    public PokedexController(
            TrainerController trainerController, PokemonController pokemonController) {
        this.trainerController = trainerController;
        this.pokemonController = pokemonController;
    }

    /**
     * Create a new Pokemon Species of the specified number by fetching data from /pokemon.json
     *
     * @param pokedexNumber pokedex number of the pokemon
     * @return Pokemon species with respect to the pokedex number.
     */
    @Nonnull
    public PokemonSpecies getPokemonSpeciesByNumber(int pokedexNumber) {
        PokemonSpeciesBuilder builder = PokemonSpecies.builder();
        builder.pokedexNumber(pokedexNumber);
        InputStream stream = this.getClass().getResourceAsStream("/pokemon.json");
        try {
            byte[] pokemonData = stream.readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper();

            List<Map<String, Object>> pokemonList =
                    objectMapper.readValue(
                            pokemonData, new TypeReference<List<Map<String, Object>>>() {});

            Map<String, Object> pokemonNode = pokemonList.get(pokedexNumber - 1);

            String name = ((Map<String, Object>) pokemonNode.get("name")).get("english").toString();
            JsonNode typesNode = objectMapper.valueToTree(pokemonNode.get("type"));

            List<PokemonType> pokemonTypes = PokemonType.getList();
            for (JsonNode type : typesNode) {
                pokemonTypes.add(PokemonType.valueOf(type.asText().toUpperCase()));
            }
            builder.name(name);
            builder.types(pokemonTypes);
            builder.imageURL(
                    String.format(
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/%s.png",
                            Integer.toString(pokedexNumber)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return Objects.requireNonNull(builder.build());
    }

    /**
     * Fetches the list of the pokemons from the trainer's pokemon collection with respect to the id
     * provided by the user.
     *
     * @param pokedexNumber is the unique identifier of the Pokemon Species.
     * @param trainerDiscordId the id assigned to the user while using the bot.
     * @return the list of fetched pokemons from mongodb database that matches the id.
     */
    @Nonnull
    public List<Pokemon> getPokemonMatchingPokedexNumber(
            Integer pokedexNumber, String trainerDiscordId) {
        List<Pokemon> retrievedPokemon = new ArrayList<>();
        Trainer trainer = trainerController.getTrainerForMemberId(trainerDiscordId);
        List<ObjectId> pokemonIds = trainer.getPokemonCollection();
        for (ObjectId pokemonObjectId : pokemonIds) {
            if (pokemonObjectId == null) continue;
            Pokemon pokemon = pokemonController.getPokemonById(pokemonObjectId);
            if (pokemon == null) continue;
            if (pokemon.getPokedexNumber().equals(pokedexNumber)) {
                retrievedPokemon.add(pokemon);
            }
        }
        return retrievedPokemon;
    }

    public Pokemon getPokemonByObjectId(@Nonnull ObjectId id) {
        return pokemonController.getPokemonById(id);
    }
}
