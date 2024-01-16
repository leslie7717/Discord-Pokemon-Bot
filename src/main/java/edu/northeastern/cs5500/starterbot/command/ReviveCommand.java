package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Slf4j
public class ReviveCommand implements SlashCommandHandler {
    static final String NAME = "revive";

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject
    public ReviveCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return NAME;
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Revive all Pokemon the trainer has");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /revive");

        // revive the Pokemon
        String trainerDiscordId = event.getMember().getId();
        trainerController.reviveTrainerPokemon(trainerDiscordId);
        event.reply(
                        String.format(
                                "All Pokemon for player <@%s> have been revived.",
                                trainerDiscordId))
                .setEphemeral(true)
                .queue();
    }
}
