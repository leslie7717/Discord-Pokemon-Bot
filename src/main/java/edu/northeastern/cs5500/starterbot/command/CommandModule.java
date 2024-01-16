package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public class CommandModule {

    @Provides
    @IntoMap
    @StringKey(SpawnCommand.NAME)
    public SlashCommandHandler provideSpawnCommand(SpawnCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(SpawnCommand.NAME)
    public ButtonHandler provideSpawnCommandClickHandler(SpawnCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(LookUpCommand.NAME)
    public SlashCommandHandler provideLookUpCommand(LookUpCommand lookUpCommand) {
        return lookUpCommand;
    }

    @Provides
    @IntoMap
    @StringKey(LookUpPokedexCommand.NAME)
    public SlashCommandHandler provideLookUpPokedexCommand(
            LookUpPokedexCommand lookUpPokedexCommand) {
        return lookUpPokedexCommand;
    }

    @Provides
    @IntoMap
    @StringKey(OffenseTypeEffectivenessCommand.NAME)
    public SlashCommandHandler provideOffenseTypeEffectivenessCommand(
            OffenseTypeEffectivenessCommand offenseTypeEffectivenessCommand) {
        return offenseTypeEffectivenessCommand;
    }

    @Provides
    @IntoMap
    @StringKey(OffenseTypeEffectivenessCommand.NAME)
    public StringSelectHandler provideOffenseTypeEffectivenessCommandMenuHandler(
            OffenseTypeEffectivenessCommand offenseTypeEffectivenessCommand) {
        return offenseTypeEffectivenessCommand;
    }

    @Provides
    @IntoMap
    @StringKey(DefenseTypeEffectivenessCommand.NAME)
    public StringSelectHandler provideDefenseTypeEffectivenessCommandMenuHandler(
            DefenseTypeEffectivenessCommand defenseTypeEffectivenessCommand) {
        return defenseTypeEffectivenessCommand;
    }

    @Provides
    @IntoMap
    @StringKey(DefenseTypeEffectivenessCommand.NAME)
    public SlashCommandHandler provideDefenseTypeEffectivenessCommand(
            DefenseTypeEffectivenessCommand defenseTypeEffectivenessCommand) {
        return defenseTypeEffectivenessCommand;
    }

    @Provides
    @IntoMap
    @StringKey(AttackMoveEffectivenessCommand.NAME)
    public SlashCommandHandler provideOffenseMoveEffectivenessCommand(
            AttackMoveEffectivenessCommand attackMoveEffectivenessCommand) {
        return attackMoveEffectivenessCommand;
    }

    @Provides
    @IntoMap
    @StringKey(TradeCommand.NAME)
    public SlashCommandHandler provideTradeCommand(TradeCommand tradeCommand) {
        return tradeCommand;
    }

    @Provides
    @IntoMap
    @StringKey(TradeCommand.NAME)
    public ButtonHandler provideTradeCommandClickHandler(TradeCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(ReviveCommand.NAME)
    public SlashCommandHandler provideReviveCommand(ReviveCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(PokemonOfDayCommand.NAME)
    public SlashCommandHandler providePokemonOfTheDayCommand(
            PokemonOfDayCommand pokemonOfDayCommand) {
        return pokemonOfDayCommand;
    }

    @Provides
    @IntoMap
    @StringKey(PokemonOfDayCommand.NAME)
    public ButtonHandler providePokemonOfTheDayCommandClickHandler(PokemonOfDayCommand command) {
        return command;
    }
}
