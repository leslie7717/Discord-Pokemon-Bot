package edu.northeastern.cs5500.starterbot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.starterbot.model.Move;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Pokemon.PokemonBuilder;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class PokemonController {
    public static final int GEN_ONE_MAX_POKEDEX_NUMBER = 151;
    public static final double CATCH_PROBABILITY = 0.7;
    private Random rand;
    private static final List<Integer> powerPokemonList =
            Arrays.asList(103, 131, 130, 142, 121, 143, 113, 150, 151, 94, 149, 65);

    GenericRepository<Pokemon> pokemonRepository;
    private final MoveController moveController;

    @Inject
    public PokemonController(
            GenericRepository<Pokemon> pokemonRepository, MoveController moveController) {
        this.pokemonRepository = pokemonRepository;
        this.moveController = moveController;
    }

    /**
     * Get random moves for a Pokemon based on its types and add them to the builder.
     *
     * @param builder The Pokemon builder to which moves will be added.
     * @param objectMapper The ObjectMapper for JSON parsing.
     * @param pokemonNode The map containing Pokemon data.
     * @param moveController The MoveController for retrieving random moves.
     */
    private void getRandomMovesForPokemon(
            PokemonBuilder builder,
            ObjectMapper objectMapper,
            Map<String, Object> pokemonNode,
            MoveController moveController) {
        // Get the types of the pokemon
        JsonNode types = objectMapper.valueToTree(pokemonNode.get("type"));
        // Get random moves from its types
        Map<String, List<Move>> movesByType = moveController.loadMovesData();
        if (types.isArray()) {
            for (final JsonNode typeNode : types) {
                String type = typeNode.asText();
                Move move = moveController.getRandomMove(type, movesByType);
                if (move != null) {
                    builder.move(move);
                }
            }
        }
    }

    /**
     * Create a new Pokemon of the specified number and add it to the repository.
     *
     * @param pokedexNumber id of the Pokemon
     * @return Pokemon object with respect to the pokedex number.
     */
    @Nonnull
    Pokemon spawnPokemon(int pokedexNumber) {
        if (pokedexNumber > GEN_ONE_MAX_POKEDEX_NUMBER || pokedexNumber < 1) {
            throw new IllegalArgumentException("Pokedex Number must be between 1 to 151");
        }
        PokemonBuilder builder = Pokemon.builder();
        builder.pokedexNumber(pokedexNumber);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream stream = this.getClass().getResourceAsStream("/pokemon.json");
            byte[] pokemonData = stream.readAllBytes();
            List<Map<String, Object>> pokemonList =
                    objectMapper.readValue(
                            pokemonData, new TypeReference<List<Map<String, Object>>>() {});

            Map<String, Object> pokemonNode = pokemonList.get(pokedexNumber - 1);
            JsonNode baseStats = objectMapper.valueToTree(pokemonNode.get("base"));

            getRandomMovesForPokemon(builder, objectMapper, pokemonNode, moveController);
            builder.currentHp(baseStats.get("HP").asInt());
            builder.hp(baseStats.get("HP").asInt());
            builder.attack(baseStats.get("Attack").asInt());
            builder.defense(baseStats.get("Defense").asInt());
            builder.specialAttack(baseStats.get("Sp. Attack").asInt());
            builder.specialDefense(baseStats.get("Sp. Defense").asInt());
            builder.speed(baseStats.get("Speed").asInt());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return pokemonRepository.add(Objects.requireNonNull(builder.build()));
    }

    public Boolean catchPokemon(ObjectId pokemonObjectId) {
        Pokemon pokemon = pokemonRepository.get(pokemonObjectId);
        pokemon.setCatchAttempted(true);
        pokemonRepository.update(pokemon);
        rand = new Random();
        return rand.nextDouble() < CATCH_PROBABILITY;
    }

    public Pokemon spawnRandomPokemon() {
        rand = new Random();
        return spawnPokemon(rand.nextInt(GEN_ONE_MAX_POKEDEX_NUMBER) + 1);
    }

    @Nullable
    public Pokemon getPokemonById(@Nonnull String pokemonId) {
        return getPokemonById(new ObjectId(pokemonId));
    }

    @Nullable
    public Pokemon getPokemonById(@Nonnull ObjectId pokemonId) {
        return pokemonRepository.get(pokemonId);
    }

    /**
     * This method iterates through the pokemon.json file and finds the appropriate node with
     * respect to the searched pokemon name.
     *
     * @param pokemonName provided by the user.
     * @return Pokemon object
     */
    @Nullable
    public Pokemon lookUpPokemonInfoByName(String pokemonName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream stream = this.getClass().getResourceAsStream("/pokemon.json");
            List<Map<String, Object>> pokemonList =
                    objectMapper.readValue(
                            stream, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> pokemon : pokemonList) {
                JsonNode pokemonNode = objectMapper.valueToTree(pokemon.get("name"));
                String name = pokemonNode.get("english").asText();
                if (name.equalsIgnoreCase(pokemonName)) {
                    PokemonBuilder builder = Pokemon.builder();

                    // Found the Pokemon node
                    JsonNode id = objectMapper.valueToTree(pokemon.get("id"));
                    builder.pokedexNumber(id.asInt());
                    JsonNode baseStats = objectMapper.valueToTree(pokemon.get("base"));
                    builder.currentHp(baseStats.get("HP").asInt());
                    builder.hp(baseStats.get("HP").asInt());
                    builder.attack(baseStats.get("Attack").asInt());
                    builder.defense(baseStats.get("Defense").asInt());
                    builder.specialAttack(baseStats.get("Sp. Attack").asInt());
                    builder.specialDefense(baseStats.get("Sp. Defense").asInt());
                    builder.speed(baseStats.get("Speed").asInt());
                    return builder.build();
                }
            }
        } catch (IOException e) {
            log.error("Unable to open /pokemon.json for reading", e);
        }
        return null;
    }

    /**
     * Create a new powerful Pokemon as Pokemon of the day.
     *
     * @return Pokemon object with respect to the random pokedex number from the list.
     */
    @Nonnull
    public Pokemon getPowerfulPokemon() {
        if (powerPokemonList != null && !powerPokemonList.isEmpty()) {
            int index = (int) (Math.random() * powerPokemonList.size());
            int powerPokemonIndex = powerPokemonList.get(index);
            return spawnPokemon(powerPokemonIndex);
        } else {
            throw new IllegalStateException("Powerful Pokemon list is empty");
        }
    }
}
