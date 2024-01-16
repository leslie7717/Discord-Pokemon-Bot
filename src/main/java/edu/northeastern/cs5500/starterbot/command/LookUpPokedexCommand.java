package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.model.Move;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.util.Iterator;
import java.util.List;
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
public class LookUpPokedexCommand implements SlashCommandHandler {

    static final String NAME = "pokedex";

    @Inject PokedexController pokedexController;

    @Inject PokemonController pokemonController;

    @Inject
    public LookUpPokedexCommand() {
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
                        "Retrieve the information of the Pokemon from the Pokedex that the user has searched for.")
                .addOption(
                        OptionType.STRING,
                        "pokemonname",
                        "The bot will reply with all the relevant details of the Pokemon.",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /pokedex");
        var option = event.getOption("pokemonname");
        if (option == null) {
            log.error("Received null value for mandatory parameter 'pokemonname'");
            return;
        }
        String trainerDiscordId = event.getMember().getId();
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        Pokemon generalPokemon = pokemonController.lookUpPokemonInfoByName(option.getAsString());

        if (generalPokemon == null) {
            event.reply(
                            String.format(
                                    "There are no known Pokemon species with a name of %s!",
                                    option.getAsString()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        final List<Pokemon> pokemonList =
                pokedexController.getPokemonMatchingPokedexNumber(
                        generalPokemon.getPokedexNumber(), trainerDiscordId);
        if (pokemonList.isEmpty()) {
            event.reply(
                            String.format(
                                    "You don't have any pokemon of species %s!",
                                    option.getAsString()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemonList.get(0).getPokedexNumber());
        StringBuilder typesWithEmojis = new StringBuilder();
        for (PokemonType type : species.getTypes()) {
            typesWithEmojis.append(PokemonType.getNamewithEmoji(type)).append("\n");
        }
        embedBuilder.setTitle(species.getName());
        embedBuilder.addField("Type", typesWithEmojis.toString(), false);
        embedBuilder.setThumbnail(species.getImageURL());
        int count = 1;
        for (Pokemon pokemon : pokemonList) {
            embedBuilder.addField(
                    "Level for "
                            + species.getName()
                            + (pokemonList.size() > 1 ? (" " + count) : "")
                            + ":",
                    Integer.toString(pokemon.getLevel()),
                    false);
            embedBuilder.addField(
                    "Basic Stats for "
                            + species.getName()
                            + (pokemonList.size() > 1 ? (" " + count) : "")
                            + ":",
                    "Current HP/HP : "
                            + Integer.toString(pokemon.getCurrentHp())
                            + "/"
                            + Integer.toString(pokemon.getHp()),
                    false);
            StringBuilder moves = new StringBuilder();
            if (pokemon.getMoves() != null && !pokemon.getMoves().isEmpty()) {
                Iterator<Move> move = pokemon.getMoves().iterator();
                while (move.hasNext()) {
                    moves.append(move.next().getName()).append("\n");
                }

                embedBuilder.addField(
                        "Moves for "
                                + species.getName()
                                + (pokemonList.size() > 1 ? (" " + count) : "")
                                + ":",
                        moves.toString(),
                        false);
            }
            count++;
        }
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
    }
}
