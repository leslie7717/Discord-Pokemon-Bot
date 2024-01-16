package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.MoveController;
import edu.northeastern.cs5500.starterbot.model.MoveEffectiveness;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Slf4j
public class DefenseTypeEffectivenessCommand implements SlashCommandHandler, StringSelectHandler {
    static final String NAME = "defensetypeeff";
    @Inject MoveController moveController;

    @Inject
    public DefenseTypeEffectivenessCommand() {
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
        return Commands.slash(getName(), "Provides effectiveness information for defense.");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /defensetypeeff");

        List<SelectOption> options = new ArrayList<>();
        for (PokemonType type : PokemonType.values()) {
            options.add(SelectOption.of(type.getEmoji() + " " + type.getName(), type.getName()));
        }

        StringSelectMenu menu =
                StringSelectMenu.create(NAME)
                        .setPlaceholder(
                                "Choose opponent's pokemon type(s)") // shows the placeholder
                        .addOptions(options)
                        .setMaxValues(2)
                        .build();
        event.reply("Choose opponent's pokemon type(s)")
                .setEphemeral(true)
                .addActionRow(menu)
                .queue();
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        final List<String> types = event.getInteraction().getValues();
        List<PokemonType> pokemonTypes = PokemonType.getList();
        for (String type : types) {
            pokemonTypes.add(PokemonType.valueOf(type.toUpperCase()));
        }

        Map<PokemonType, MoveEffectiveness> effects =
                moveController.getEffectivenessDefense(pokemonTypes);

        StringBuilder content = new StringBuilder();
        for (Entry<PokemonType, MoveEffectiveness> effect : effects.entrySet()) {
            MoveEffectiveness moveEffectiveness = effect.getValue();
            PokemonType pokemonType = effect.getKey();
            content.append(moveEffectiveness.getEffectiveness())
                    .append("x to ")
                    .append(PokemonType.getNamewithEmoji(pokemonType))
                    .append("\n");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                String.format("%s %s take:", types.get(0), types.size() > 1 ? types.get(1) : ""));
        embedBuilder.addField("", content.toString(), false);
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
    }
}
