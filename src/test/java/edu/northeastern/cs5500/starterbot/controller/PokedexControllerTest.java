package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * PokedexControllerTest tests if an entry gets added to the Pokedex correctly and
 *  each Pokemon is added to pokemon collection
 * for each trainer id. For testing pupose InMemoryRepository is used as a mockup of MongoDBRepository,
 * to test add, update and get operation after a catch operation.
 */
public class PokedexControllerTest {

    private TrainerController trainerController;
    private PokemonController pokemonController;
    private PokedexController pokedexController;

    @BeforeEach
    void setupPokedexController() {
        pokemonController = new PokemonController(new InMemoryRepository<>(), new MoveController());
        trainerController = new TrainerController(new InMemoryRepository<>(), pokemonController);
        pokedexController = new PokedexController(trainerController, pokemonController);
    }

    @Test
    void testGetPokemonSpeciesByNumber() {
        int pokedexNumber = 1;
        PokemonSpecies result = pokedexController.getPokemonSpeciesByNumber(pokedexNumber);
        assertThat(result).isNotNull();
        assertThat(result.getPokedexNumber()).isEqualTo(pokedexNumber);
    }

    /*
     * This method tests if we have retrieve the correct pokemon from Pokedex
     * for a trainer with respect to a pokemon id. Failure indicates bug in controller.
     *
     * This is a positive test.
     */
    @Test
    void testGetPokemonMatchingPokedexNumber() {
        Trainer trainer = new Trainer();
        trainer.setDiscordUserId("1");
        Pokemon randomPokemon = pokemonController.spawnPokemon(1);

        trainerController.addPokemonToTrainer(trainer.getDiscordUserId(), randomPokemon);

        List<Pokemon> pokemonList =
                pokedexController.getPokemonMatchingPokedexNumber(
                        randomPokemon.getPokedexNumber(), trainer.getDiscordUserId());

        assertThat(pokemonList).containsExactly(randomPokemon);
    }
}
