package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.MoveController;
import edu.northeastern.cs5500.starterbot.model.MoveEffectiveness;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Slf4j
public class AttackMoveEffectivenessCommand implements SlashCommandHandler {
    static final String NAME = "attackmoveeff";

    @Inject MoveController moveController;

    @Inject
    public AttackMoveEffectivenessCommand() {
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
        return Commands.slash(getName(), "Provides attacker move effectiveness against all types.")
                .addOption(OptionType.STRING, "movename", "Attacker Move Name", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /offenseMoveEff");
        var moveName = event.getOption("movename");
        if (moveName == null) {
            log.error("Received null value for mandatory parameter 'movename'");
            return;
        }

        Map<PokemonType, MoveEffectiveness> effects =
                moveController.getMoveEffectiveness(moveName.getAsString());
        if (effects != null) {
            StringBuilder content = new StringBuilder();
            for (Entry<PokemonType, MoveEffectiveness> effect : effects.entrySet()) {
                MoveEffectiveness moveEffectiveness = effect.getValue();
                PokemonType pokemonType = effect.getKey();
                content.append(moveEffectiveness.getEffectiveness())
                        .append("x to ")
                        .append(PokemonType.getNamewithEmoji(effect.getKey()))
                        .append("\n");
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(String.format("%s move deals:", moveName.getAsString()));
            embedBuilder.addField("", content.toString(), false);
            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
            messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());

            event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
        } else {
            event.reply(String.format("%s move could not be found!", moveName.getAsString()))
                    .setEphemeral(true)
                    .queue();
        }
    }
}
