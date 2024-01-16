package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trade;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.model.UserPreference;

@Module
public class RepositoryModule {

    @Provides
    public GenericRepository<UserPreference> provideUserPreferencesRepository(
            InMemoryRepository<UserPreference> repository) {
        return repository;
    }

    @Provides
    public GenericRepository<Pokemon> providePokemonRepository(
            MongoDBRepository<Pokemon> repository) {
        return repository;
    }

    @Provides
    public GenericRepository<Trainer> provideTrainerRepository(
            MongoDBRepository<Trainer> repository) {
        return repository;
    }

    @Provides
    public GenericRepository<Trade> provideTradeRepository(MongoDBRepository<Trade> repository) {
        return repository;
    }

    @Provides
    public Class<UserPreference> provideUserPreference() {
        return UserPreference.class;
    }

    @Provides
    public Class<Pokemon> providePokemon() {
        return Pokemon.class;
    }

    @Provides
    public Class<Trainer> provideTrainer() {
        return Trainer.class;
    }

    @Provides
    public Class<Trade> provideTrade() {
        return Trade.class;
    }
}
