package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
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
public class LookUpCommand implements SlashCommandHandler {

    static final String NAME = "lookup";

    @Inject PokemonController pokemonController;

    @Inject PokedexController pokedexController;

    @Inject
    public LookUpCommand() {
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
        return Commands.slash(
                        getName(),
                        "Retrieve the information of the Pokemon that the user has searched for.")
                .addOption(
                        OptionType.STRING,
                        "pokemonname",
                        "The bot will reply with the Pokemon details.",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /lookup");

        var option = event.getOption("pokemonname");
        if (option == null) {
            log.error("Received null value for mandatory parameter 'pokemonname'");
            return;
        }
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Pokemon pokemon = pokemonController.lookUpPokemonInfoByName(option.getAsString());
        if (pokemon != null) {
            PokemonSpecies species =
                    pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());
            StringBuilder typesWithEmojis = new StringBuilder();
            for (PokemonType type : species.getTypes()) {
                typesWithEmojis.append(PokemonType.getNamewithEmoji(type)).append("\n");
            }
            embedBuilder.setTitle(species.getName());
            embedBuilder.addField("Type", typesWithEmojis.toString(), false);
            embedBuilder.setThumbnail(species.getImageURL());
            embedBuilder.addField("HP", Integer.toString(pokemon.getHp()), false);

            messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
            event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
        } else {
            event.reply(
                            String.format(
                                    "The searched pokemon name %s could not be found!",
                                    option.getAsString()))
                    .setEphemeral(true)
                    .queue();
        }
    }
}
