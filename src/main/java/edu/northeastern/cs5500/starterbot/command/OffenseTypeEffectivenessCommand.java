package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.MoveController;
import edu.northeastern.cs5500.starterbot.model.MoveEffectiveness;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
public class OffenseTypeEffectivenessCommand implements SlashCommandHandler, StringSelectHandler {
    static final String NAME = "offensetypeeff";

    @Inject MoveController moveController;

    @Inject
    public OffenseTypeEffectivenessCommand() {
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
        return Commands.slash(getName(), "Gives pokemon type menu");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /offensetypeeff");

        List<SelectOption> options = new ArrayList<>();
        for (PokemonType type : PokemonType.values()) {
            options.add(SelectOption.of(PokemonType.getNamewithEmoji(type), type.getName()));
        }

        StringSelectMenu menu =
                StringSelectMenu.create(NAME)
                        .setPlaceholder(
                                "Choose your pokemon type") // shows the placeholder indicating what
                        .addOptions(options)
                        .build();
        event.reply("Choose your pokemon type").setEphemeral(true).addActionRow(menu).queue();
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);

        PokemonType ptype = PokemonType.valueOf(response.toUpperCase());
        Map<PokemonType, MoveEffectiveness> effects = moveController.getEffectivenessOffense(ptype);

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
        embedBuilder.setTitle(String.format("%s-Type deals:", response));
        embedBuilder.addField("", content.toString(), false);
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
    }
}
