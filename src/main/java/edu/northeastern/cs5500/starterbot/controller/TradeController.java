package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.NonNull;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trade;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class TradeController {
    GenericRepository<Trade> tradeRepository;
    TrainerController trainerController;
    PokedexController pokedexController;

    @Inject
    public TradeController(
            GenericRepository<Trade> tradeRepository,
            TrainerController trainerController,
            PokedexController pokedexController) {
        this.tradeRepository = tradeRepository;
        this.trainerController = trainerController;
        this.pokedexController = pokedexController;
    }

    /**
     * Creates a new trade offering initiated by the specified trainer and add it to trade
     * repository.
     *
     * @param trainer Trainer who initiates the trade
     * @param offerPokemonPokedexNumber Pokedex Number of the pokemon being offered
     * @param requestPokemonPokedexNumber Pokedex Number of the pokemon being requested.
     * @throws IllegalArgumentException If the requested Pokemon is not in the trainer's Pokedex.
     * @return
     */
    public Trade createNewOffering(
            Trainer trainer, Integer offerPokemonPokedexNumber, int requestPokemonPokedexNumber) {

        // Checks if trainer has the pokemon being offered
        List<Pokemon> pokemons =
                pokedexController.getPokemonMatchingPokedexNumber(
                        offerPokemonPokedexNumber, trainer.getDiscordUserId());
        if (pokemons.size() == 0) {
            throw new IllegalArgumentException("Offered pokemon not in pokedex");
        }

        // add trade to trade repository
        Trade trade =
                new Trade(trainer.getId(), pokemons.get(0).getId(), requestPokemonPokedexNumber);
        return tradeRepository.add(trade);
    }

    /**
     * Accepts an offering in a trade. Initiates the exchange process, updates trainers'
     * repositories, and removes the completed trade from the trade repository.
     *
     * @param trainer Trainer who accepted the trade
     * @param id trade object id
     * @throws IllegalStateException If the trade is closed or not found in the repository.
     * @throws IllegalArgumentException If the requested Pokemon is not in the trainer's Pokedex.
     */
    public void acceptOffering(Trainer trainer, @NonNull ObjectId id) {
        // get trade from db. If null trade is closed.
        Trade trade = null;
        if (id != null) {
            trade = tradeRepository.get(id);
        }
        if (trade == null) {
            throw new IllegalStateException("Trade is closed");
        }

        // Checks if trainer has the requested pokemon
        List<Pokemon> pokemons =
                pokedexController.getPokemonMatchingPokedexNumber(
                        trade.getRequestPokemonPokedexNumber(), trainer.getDiscordUserId());
        if (pokemons.size() == 0) {
            throw new IllegalArgumentException("Requested pokemon not in pokedex");
        }

        // Initiate exchange
        updateTrainersRepository(trade, trainer, pokemons.get(0));

        // delete the trade on trade completion
        tradeRepository.delete(trade.getId());
    }

    /**
     * Updates trainer's repositories and pokedex
     *
     * @param trade
     * @param requesttrainer Trainer who accepted the trade
     * @param requestPokemon Pokemon requested in the trade
     */
    private void updateTrainersRepository(
            Trade trade, Trainer requesttrainer, Pokemon requestPokemon) {

        // Remove Pokemon from Trainers
        trainerController.removePokemonFromTrainer(
                trainerController.getTrainerForId(trade.getTrainerId()).getDiscordUserId(),
                pokedexController.getPokemonByObjectId(trade.getOfferPokemon()));

        trainerController.removePokemonFromTrainer(
                requesttrainer.getDiscordUserId(), requestPokemon);

        // Add Pokemon to Trainers
        trainerController.addPokemonToTrainer(
                trainerController.getTrainerForId(trade.getTrainerId()).getDiscordUserId(),
                requestPokemon);

        trainerController.addPokemonToTrainer(
                requesttrainer.getDiscordUserId(),
                pokedexController.getPokemonByObjectId(trade.getOfferPokemon()));
    }
}
