package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.model.Trade;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TradeControllerTest {

    final String DISCORD_USER_ID_1 = "discordUserId1";
    final String DISCORD_USER_ID_2 = "discordUserId2";
    final String DISCORD_USER_ID_3 = "discordUserId3";

    final Integer OFFERED_POKEMON_POKEDEX_NUMBER = 1;
    final Integer REQUESTED_POKEMON_POKEDEX_NUMBER = 4;

    TrainerController trainerController;
    PokedexController pokedexController;
    PokemonController pokemonController;
    TradeController tradeController;

    @BeforeEach
    void setupTradeController() {
        pokemonController = new PokemonController(new InMemoryRepository<>(), new MoveController());

        trainerController = new TrainerController(new InMemoryRepository<>(), pokemonController);
        trainerController.addPokemonToTrainer(
                DISCORD_USER_ID_1, pokemonController.spawnPokemon(OFFERED_POKEMON_POKEDEX_NUMBER));
        trainerController.addPokemonToTrainer(
                DISCORD_USER_ID_2,
                pokemonController.spawnPokemon(REQUESTED_POKEMON_POKEDEX_NUMBER));
        trainerController.addPokemonToTrainer(
                DISCORD_USER_ID_3,
                pokemonController.spawnPokemon(REQUESTED_POKEMON_POKEDEX_NUMBER));

        pokedexController = new PokedexController(trainerController, pokemonController);
        tradeController =
                new TradeController(
                        new InMemoryRepository<>(), trainerController, pokedexController);
    }

    /**
     * Test for creating a new trade and adding to trade repository. If fails bug must be
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testThatTradeOfferingIsCreated() {
        Trade testTrade =
                tradeController.createNewOffering(
                        trainerController.getTrainerForMemberId(DISCORD_USER_ID_1),
                        OFFERED_POKEMON_POKEDEX_NUMBER,
                        REQUESTED_POKEMON_POKEDEX_NUMBER);

        Trade expectedTrade = tradeController.tradeRepository.get(testTrade.getId());

        // Positive test to check if trade was created
        assertThat(expectedTrade.getOfferPokemon()).isEqualTo(testTrade.getOfferPokemon());
    }

    /**
     * Test for creating a trade when the offered pokemon is not in trainer's repository. If fails
     * bug must be controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testThatOfferedPokemonIsInTrainerCollection() {
        trainerController.getTrainerForMemberId(DISCORD_USER_ID_1).getPokemonCollection().clear();

        // Must throw exception if the pokemon is not in the the trainer's pokedex
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            tradeController.createNewOffering(
                                    trainerController.getTrainerForMemberId(DISCORD_USER_ID_1),
                                    OFFERED_POKEMON_POKEDEX_NUMBER,
                                    REQUESTED_POKEMON_POKEDEX_NUMBER);
                        });

        String expectedMessage = "Offered pokemon not in pokedex";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    /**
     * Test for checking that on trade acceptance trainers repository and pokedex is updated
     * correctly . If fails bug must be controller. Test will not provide false positves or false
     * negatives.
     */
    @Test
    void testThatAcceptTradeIsWorking() {

        ObjectId offeredPokemonObjectId =
                pokedexController
                        .getPokemonMatchingPokedexNumber(
                                OFFERED_POKEMON_POKEDEX_NUMBER, DISCORD_USER_ID_1)
                        .get(0)
                        .getId();
        ObjectId requestedPokemonObjectId =
                pokedexController
                        .getPokemonMatchingPokedexNumber(
                                REQUESTED_POKEMON_POKEDEX_NUMBER, DISCORD_USER_ID_2)
                        .get(0)
                        .getId();

        Trade testTrade =
                tradeController.createNewOffering(
                        trainerController.getTrainerForMemberId(DISCORD_USER_ID_1),
                        OFFERED_POKEMON_POKEDEX_NUMBER,
                        REQUESTED_POKEMON_POKEDEX_NUMBER);

        tradeController.acceptOffering(
                trainerController.getTrainerForMemberId(DISCORD_USER_ID_2),
                new ObjectId(testTrade.getId().toString()));

        // Tests to see if the trainers pokemon collection is updated
        assertThat(
                        trainerController
                                .getTrainerForMemberId(DISCORD_USER_ID_2)
                                .getPokemonCollection()
                                .size())
                .isEqualTo(1);

        assertThat(
                        trainerController
                                .getTrainerForMemberId(DISCORD_USER_ID_2)
                                .getPokemonCollection()
                                .get(0))
                .isEqualTo(offeredPokemonObjectId);

        assertThat(
                        trainerController
                                .getTrainerForMemberId(DISCORD_USER_ID_1)
                                .getPokemonCollection()
                                .size())
                .isEqualTo(1);
        assertThat(
                        trainerController
                                .getTrainerForMemberId(DISCORD_USER_ID_1)
                                .getPokemonCollection()
                                .get(0))
                .isEqualTo(requestedPokemonObjectId);
    }

    /**
     * Test to check if requested pokemon is in trainers collection. If fails bug must be
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testThatRequestedPokemonIsInTrainerCollection() {

        trainerController.getTrainerForMemberId(DISCORD_USER_ID_2).getPokemonCollection().clear();

        Trade testTrade =
                tradeController.createNewOffering(
                        trainerController.getTrainerForMemberId(DISCORD_USER_ID_1),
                        OFFERED_POKEMON_POKEDEX_NUMBER,
                        REQUESTED_POKEMON_POKEDEX_NUMBER);
        // Throws exception if pokemon not in trainers collection
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            tradeController.acceptOffering(
                                    trainerController.getTrainerForMemberId(DISCORD_USER_ID_2),
                                    new ObjectId(testTrade.getId().toString()));
                        });

        String expectedMessage = "Requested pokemon not in pokedex";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);

        // test to assert that trade is not completed if the pokemon is not there in trainers
        // collection
        assertThat(0)
                .isEqualTo(
                        pokedexController
                                .getPokemonMatchingPokedexNumber(
                                        OFFERED_POKEMON_POKEDEX_NUMBER, DISCORD_USER_ID_2)
                                .size());
        assertThat(1)
                .isEqualTo(
                        pokedexController
                                .getPokemonMatchingPokedexNumber(
                                        OFFERED_POKEMON_POKEDEX_NUMBER, DISCORD_USER_ID_1)
                                .size());
    }

    /**
     * Test to check that the trade can be accepted only once. If fails bug must be controller. Test
     * will not provide false positves or false negatives.
     */
    @Test
    void testThatTradeCanHappenOnce() {

        // DISCORD_USER_ID_1 initiates the trade
        Trade testTrade =
                tradeController.createNewOffering(
                        trainerController.getTrainerForMemberId(DISCORD_USER_ID_1),
                        OFFERED_POKEMON_POKEDEX_NUMBER,
                        REQUESTED_POKEMON_POKEDEX_NUMBER);

        // DISCORD_USER_ID_2 accepts the trade
        tradeController.acceptOffering(
                trainerController.getTrainerForMemberId(DISCORD_USER_ID_2),
                new ObjectId(testTrade.getId().toString()));

        // DISCORD_USER_ID_3 also tries to accept the trade.
        // As the trade already happened between DISCORD_USER_ID_1 and DISCORD_USER_ID_2,
        // DISCORD_USER_ID_3 cannot trade anymore.
        Exception exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> {
                            tradeController.acceptOffering(
                                    trainerController.getTrainerForMemberId(DISCORD_USER_ID_3),
                                    new ObjectId(testTrade.getId().toString()));
                        });

        String expectedMessage = "Trade is closed";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);

        // Test to check that the requested pokemon is not traded and is still in  DISCORD_USER_ID_3
        // repository.
        assertThat(0)
                .isEqualTo(
                        pokedexController
                                .getPokemonMatchingPokedexNumber(1, DISCORD_USER_ID_3)
                                .size());
    }
}
