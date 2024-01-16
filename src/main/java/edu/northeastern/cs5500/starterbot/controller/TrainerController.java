package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class TrainerController {
    GenericRepository<Trainer> trainerRepository;
    PokemonController pokemonController;

    @Inject
    public TrainerController(
            GenericRepository<Trainer> trainerRepository, PokemonController pokemonController) {
        this.trainerRepository = trainerRepository;
        this.pokemonController = pokemonController;
    }

    @Nonnull
    public Trainer getTrainerForMemberId(String discordMemberId) {
        Collection<Trainer> trainers = trainerRepository.getAll();
        for (Trainer currentTrainer : trainers) {
            if (currentTrainer.getDiscordUserId().equals(discordMemberId)) {
                return currentTrainer;
            }
        }

        Trainer trainer = new Trainer();
        trainer.setDiscordUserId(discordMemberId);
        return trainerRepository.add(trainer);
    }

    public Trainer addPokemonToTrainer(String discordMemberId, Pokemon pokemon) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        if (pokemon != null && !trainer.getPokemonCollection().contains(pokemon.getId())) {
            trainer.getPokemonCollection().add(pokemon.getId());
            return trainerRepository.update(trainer);
        }
        return trainer;
    }

    public Trainer removePokemonFromTrainer(String discordMemberId, Pokemon pokemon) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        if (pokemon != null) {
            trainer.getPokemonCollection().remove(pokemon.getId());
            trainerRepository.update(trainer);
        }
        return trainer;
    }

    public Trainer getTrainerForId(@Nonnull ObjectId trainerId) {
        return trainerRepository.get(trainerId);
    }

    public Trainer reviveTrainerPokemon(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        List<ObjectId> trainerPokemonCollection = trainer.getPokemonCollection();
        for (ObjectId pokemonId : trainerPokemonCollection) {
            if (pokemonId == null) continue;
            Pokemon pokemon = pokemonController.getPokemonById(pokemonId);
            if (pokemon != null) {
                Integer fullHp = pokemon.getHp();
                pokemon.setCurrentHp(fullHp);
                trainerRepository.update(trainer);
            }
        }
        return trainer;
    }

    /*
     * Checks if the time difference between now and the last used pokemon of the day
     * for the specific trainer. If time difference is greater than or equal to 24 hours
     * the powerful pokemon will be shown.
     *
     * @param eventTimeStamp the time stamp of the pokemon of the day usage.
     * @param trainerDiscordId id of the trainer
     *
     * @return boolean value based on the which the pokemon will be shown or not.
     *
     */
    public boolean checkIfTimeSpanIsOneDay(String eventTimeStamp, String trainerDiscordId) {
        Trainer trainer = getTrainerForMemberId(trainerDiscordId);
        boolean overTwentyFourHours =
                checkTimeDuration(eventTimeStamp, trainer.getPokemonOfDayTimeStamp());
        if (overTwentyFourHours) {
            trainer.setPokemonOfDayTimeStamp(eventTimeStamp);
            trainerRepository.update(trainer);
        }
        return overTwentyFourHours;
    }

    private boolean checkTimeDuration(String recentTimeStamp, String lastTimeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (lastTimeStamp == null || lastTimeStamp.equals(" ")) {
                return true;
            }
            Date d1 = sdf.parse(lastTimeStamp);
            Date d2 = sdf.parse(recentTimeStamp);
            long duration = d2.getTime() - d1.getTime();
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            return diffInHours >= 24;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
