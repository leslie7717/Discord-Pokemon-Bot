package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrainerControllerTest {
    private TrainerController trainerController;
    private PokemonController pokemonController;
    private PokedexController pokedexController;
    private Trainer trainer;
    final String DISCORD_USER_ID_1 = "testuser1";

    @BeforeEach
    void setupPokedexController() {
        pokemonController = new PokemonController(new InMemoryRepository<>(), new MoveController());
        trainerController = new TrainerController(new InMemoryRepository<>(), pokemonController);
        pokedexController = new PokedexController(trainerController, pokemonController);
        trainer = new Trainer();
        trainer.setDiscordUserId(DISCORD_USER_ID_1);
    }

    @Test
    public void testRevivingTrainerPokemon() {

        trainer.setDiscordUserId("existingId");
        // Add multiple Pokémon with reduced HP to a trainer
        Pokemon pokemon1 = pokemonController.spawnPokemon(1);
        pokemon1.setHp(100);
        pokemon1.setCurrentHp(50);

        Pokemon pokemon2 = pokemonController.spawnPokemon(2);
        pokemon2.setHp(120);
        pokemon2.setCurrentHp(30);

        trainerController.addPokemonToTrainer(trainer.getDiscordUserId(), pokemon1);
        trainerController.addPokemonToTrainer(trainer.getDiscordUserId(), pokemon2);

        // Revive the trainer's Pokémon
        trainerController.reviveTrainerPokemon("existingId");

        // Retrieve the list of Pokémon and check if each Pokémon's HP is fully restored
        List<Pokemon> pokemonList =
                trainer
                        .getPokemonCollection() // Assuming this gets the collection of Pokémon IDs
                        .stream()
                        .map(pokemonId -> pokemonController.getPokemonById(pokemonId))
                        .collect(Collectors.toList());

        for (Pokemon pokemon : pokemonList) {
            assertThat(pokemon.getCurrentHp()).isEqualTo(pokemon.getHp());
        }
    }

    /*
     * This method tests when we create an instance of a trainer, with no object id set
     * it generates an object id. Failure indicates bug in controller. No false positive or false negative.
     *
     * This is a positive test.
     */
    @Test
    public void testGetTrainerForMemberId() {
        Trainer result = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        assertThat(trainer.getDiscordUserId()).isEqualTo(result.getDiscordUserId());
        assertThat(result.getId()).isNotNull();
    }

    /*
     * This method tests adding multiple pokemon to trainer pokemon collection.
     * Failure indicates bug in controller. No false positive or false negative.
     *
     * This is a positive test.
     */
    @Test
    public void testAddMultiplePokemonToTrainer() {
        Pokemon pokemon = pokemonController.spawnPokemon(1);
        Pokemon anotherPokemon = pokemonController.spawnPokemon(1);
        trainerController.addPokemonToTrainer(DISCORD_USER_ID_1, pokemon);
        trainerController.addPokemonToTrainer(trainer.getDiscordUserId(), anotherPokemon);
        List<Pokemon> pokemonList =
                pokedexController.getPokemonMatchingPokedexNumber(
                        pokemon.getPokedexNumber(), trainer.getDiscordUserId());
        assertThat(pokemonList.size()).isEqualTo(2);
    }

    /*
     * This method tests if we can successfully remove a pokemon from pokemon collection.
     * Failure indicates bug in controller. No false positive or negative.
     *
     * This is a positive test.
     */
    @Test
    public void testRemovePokemonFromTrainer() {
        Pokemon pokemon = pokemonController.spawnPokemon(10);
        trainerController.addPokemonToTrainer(trainer.getDiscordUserId(), pokemon);
        List<Pokemon> pokemonList =
                pokedexController.getPokemonMatchingPokedexNumber(
                        pokemon.getPokedexNumber(), trainer.getDiscordUserId());
        assertThat(pokemonList.size()).isEqualTo(1);
        assertThat(pokemonList).contains(pokemon);
        trainerController.removePokemonFromTrainer(trainer.getDiscordUserId(), pokemon);
        List<Pokemon> updatedList =
                pokedexController.getPokemonMatchingPokedexNumber(
                        pokemon.getPokedexNumber(), trainer.getDiscordUserId());
        assertThat(updatedList).doesNotContain(pokemon);
        assertThat(updatedList.size()).isEqualTo(0);
    }

    /*
     * This method tests adding a null Pokemon to Trainer pokemon collection. Failure indicates bug in
     * controller. No false positive or negative.
     *
     * This is a negative test.
     */
    @Test
    void testAddNullPokemonToTrainer() {
        Trainer updatedTrainer = trainerController.addPokemonToTrainer(DISCORD_USER_ID_1, null);
        assertThat(updatedTrainer).isNotNull();
        assertThat(updatedTrainer.getPokemonCollection()).isEmpty();
    }

    /*
     * This method tests removing a null Pokemon from Trainer pokemon collection. Failure indicates bug in
     * controller. No false positive or negative.
     *
     * This is a negative test.
     */
    @Test
    void testRemoveNullPokemonFromTrainer() {
        Pokemon pokemon = pokemonController.spawnPokemon(10);
        trainerController.addPokemonToTrainer(trainer.getDiscordUserId(), pokemon);
        List<Pokemon> pokemonList =
                pokedexController.getPokemonMatchingPokedexNumber(
                        pokemon.getPokedexNumber(), trainer.getDiscordUserId());
        assertThat(pokemonList.size()).isEqualTo(1);
        assertThat(pokemonList).contains(pokemon);

        Trainer updatedTrainer =
                trainerController.removePokemonFromTrainer(DISCORD_USER_ID_1, null);
        assertThat(updatedTrainer).isNotNull();
        assertThat(updatedTrainer.getPokemonCollection().size()).isEqualTo(1);
    }

    @Test
    /*
     * This method tests getting a trainer with null id. Failure indicates bug in
     * controller. No false positive or negative.
     *
     * This is a negative test.
     */
    void testGetTrainerForNullId() {
        Trainer trainer = trainerController.getTrainerForId(null);
        assertThat(trainer).isNull();
    }

    /*
     * This method tests getting the time interval if the last noted time is blank. Failure indicates bug in
     * controller. No false positive or negative.
     *
     * This is a positive test.
     */
    @Test
    public void testCheckIfTimeSpanIsOneDay() {
        String recentTimeStamp = "2023-01-01 12:00:00";
        boolean result =
                trainerController.checkIfTimeSpanIsOneDay(
                        recentTimeStamp, trainer.getDiscordUserId());
        assertThat(result).isTrue();
    }

    /*
     * This method tests getting the time interval is less than 24 hours. Failure indicates bug in
     * controller. No false positive or negative.
     *
     * This is a positive test.
     */
    @Test
    public void testCheckTimeDurationWhichIsLess() {
        String recentTimeStamp = "2023-01-01 12:00:00";
        boolean result =
                trainerController.checkIfTimeSpanIsOneDay(
                        recentTimeStamp, trainer.getDiscordUserId());
        assertThat(result).isTrue();
        ;
        String lastTimeStamp = "2023-01-01 13:00:00";
        boolean updatedResult =
                trainerController.checkIfTimeSpanIsOneDay(
                        lastTimeStamp, trainer.getDiscordUserId());
        assertThat(updatedResult).isFalse();
    }

    /*
     * This method tests getting the time interval is more than 24 hours. Failure indicates bug
     * in controller. No false positive or negative.
     *
     * This is a positive test.
     */
    @Test
    public void testCheckTimeDurationWhichIsMore() {
        String recentTimeStamp = "2023-01-01 12:00:00";
        boolean result =
                trainerController.checkIfTimeSpanIsOneDay(
                        recentTimeStamp, trainer.getDiscordUserId());
        assertThat(result).isTrue();
        String lastTimeStamp = "2023-01-03 12:00:00";
        boolean updatedResult =
                trainerController.checkIfTimeSpanIsOneDay(
                        lastTimeStamp, trainer.getDiscordUserId());
        assertThat(updatedResult).isTrue();
    }
}
