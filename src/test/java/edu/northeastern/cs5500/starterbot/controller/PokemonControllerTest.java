package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * PokemonControllerTest class is responsible for testing basic features in Pokemon class
 *
 */
class PokemonControllerTest {
    PokemonController pokemonController;

    @BeforeEach
    void setupTradeController() {
        pokemonController = new PokemonController(new InMemoryRepository<>(), new MoveController());
    }

    /**
     * Positive test for spawning a Pokemon given the Pokedex number. If fails bug must be in
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testThatControllerCanSpawnPokemon() {
        int pokedexNumber = 1;
        Pokemon spawnedPokemon = pokemonController.spawnPokemon(pokedexNumber);
        assertThat(spawnedPokemon).isNotNull();
        assertThat(spawnedPokemon.getPokedexNumber()).isEqualTo(pokedexNumber);
    }
    /**
     * Negative test for spawning a Pokemon given invalid Pokedex number. If fails bug must be in
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testSpawnPokemonWithNegativePokedexNumber() {
        int pokedexNumber = -1;
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            pokemonController.spawnPokemon(pokedexNumber);
                        });

        String expectedMessage = "Pokedex Number must be between 1 to 151";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
    /**
     * Negative test for spawning a Pokemon given invalid Pokedex number. If fails bug must be in
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testSpawnPokemonWithZeroPokedexNumber() {
        int pokedexNumber = 0;
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            pokemonController.spawnPokemon(pokedexNumber);
                        });

        String expectedMessage = "Pokedex Number must be between 1 to 151";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    /**
     * Negative test for spawning a Pokemon given invalid Pokedex number. If fails bug must be in
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testSpawnPokemonWithHighPokedexNumber() {
        int pokedexNumber = 152;
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            pokemonController.spawnPokemon(pokedexNumber);
                        });

        String expectedMessage = "Pokedex Number must be between 1 to 151";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    /**
     * Negative test for spawning a Pokemon given boundary Pokedex number. If fails bug must be in
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testSpawnPokemonWithMaximumPokedexNumber() {
        int pokedexNumber = 151;
        Pokemon spawnedPokemon = pokemonController.spawnPokemon(pokedexNumber);
        assertThat(spawnedPokemon).isNotNull();
        assertThat(spawnedPokemon.getPokedexNumber()).isEqualTo(pokedexNumber);
    }

    /**
     * Tests if we are getting null given invalid pokemon name. Test fail indicates bug in
     * controller. No false positves or false negatives.
     *
     * <p>This is a negative test.
     */
    @Test
    void testGetPokemonWithInvalidName() {
        Pokemon generalPokemon = pokemonController.lookUpPokemonInfoByName("Bulbasaur124");
        assertThat(generalPokemon).isNull();
    }

    @Test
    void testCatchPokemon() {
        Pokemon spawnedPokemon = pokemonController.spawnPokemon(1);
        boolean result = pokemonController.catchPokemon(spawnedPokemon.getId());
        assertThat(result).isNotNull();
    }

    @Test
    void testSpawnRandomPokemon() {
        Pokemon randomPokemon = pokemonController.spawnRandomPokemon();
        assertThat(randomPokemon).isNotNull();
    }

    @Test
    void testSpawnRandomPokemonMove() {
        Pokemon randomPokemon = pokemonController.spawnRandomPokemon();
        assertThat(randomPokemon.getMoves()).isNotNull();
    }

    @Test
    void testGetPokemonById() {
        int pokedexNumber = 1;
        Pokemon pokemon = pokemonController.spawnPokemon(pokedexNumber);
        assertThat(pokemonController.getPokemonById(pokemon.getId())).isEqualTo(pokemon);
    }

    /**
     * Tests if we are getting the correct Pokemon by name. If fails bug must be in controller. Test
     * will not provide false positves or false negatives.
     *
     * <p>This is a positive test.
     */
    @Test
    void testGetPokemonByName() {
        String pokemonName = "Bulbasaur";
        int pokedexNumber = 1;
        Pokemon pokemon = pokemonController.lookUpPokemonInfoByName(pokemonName);
        Pokemon anotherPokemon = pokemonController.spawnPokemon(pokedexNumber);
        assertThat(pokemon).isNotNull();
        assertThat(pokemon.getPokedexNumber()).isEqualTo(anotherPokemon.getPokedexNumber());
    }

    /**
     * Positive test for spawning a powerful Pokemon of the day. If fails bug must be in controller.
     * Test will not provide false positves or false negatives.
     */
    @Test
    void testPokemonOfTheDay() {
        List<Integer> powerPokemonList =
                Arrays.asList(103, 131, 130, 142, 121, 143, 113, 150, 151, 94, 149, 65);
        Pokemon pokemon = pokemonController.getPowerfulPokemon();
        assertThat(pokemon).isNotNull();
        assertThat(powerPokemonList).contains(pokemon.getPokedexNumber());
    }
}
