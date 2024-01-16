package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Move;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Slf4j
public class PokemonOfDayCommand implements SlashCommandHandler, ButtonHandler {

    static final String NAME = "powerfulpokemon";

    @Inject PokemonController pokemonController;

    @Inject PokedexController pokedexController;

    @Inject TrainerController trainerController;

    @Inject
    public PokemonOfDayCommand() {
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
                getName(), "Spawn a strong Pokemon with high HP, Speed for the user to catch.");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /powerfulpokemon");
        OffsetDateTime timestamp = event.getTimeCreated();
        String formattedTimestamp =
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String trainerDiscordId = event.getMember().getId();
        boolean shouldEnable =
                trainerController.checkIfTimeSpanIsOneDay(formattedTimestamp, trainerDiscordId);
        if (shouldEnable) {
            Pokemon pokemon = pokemonController.getPowerfulPokemon();
            PokemonSpecies species =
                    pokedexController.getPokemonSpeciesByNumber(
                            pokemon != null ? pokemon.getPokedexNumber() : 0);

            StringBuilder typesWithEmojis = new StringBuilder();
            for (PokemonType type : species.getTypes()) {
                typesWithEmojis.append(PokemonType.getNamewithEmoji(type)).append("\n");
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(
                    String.format("Pokemon of the day is %s!", species.getName().toUpperCase()));
            embedBuilder.addField("Type", typesWithEmojis.toString(), false);
            embedBuilder.addField("Level", Integer.toString(pokemon.getLevel()), false);
            embedBuilder.setThumbnail(species.getImageURL());

            StringBuilder moves = new StringBuilder();
            if (pokemon.getMoves() != null && !pokemon.getMoves().isEmpty()) {
                Iterator<Move> move = pokemon.getMoves().iterator();
                while (move.hasNext()) {
                    moves.append(move.next().getName()).append("\n");
                }

                embedBuilder.addField("Moves", moves.toString(), false);
            }

            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
            messageCreateBuilder =
                    messageCreateBuilder.addActionRow(
                            Button.primary(
                                    getName() + ":catch:" + pokemon.getId().toString(), "Catch"));
            messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());

            event.reply(messageCreateBuilder.build()).queue();
        } else {
            event.reply(String.format("Come back later to view the Pokemon of the day."))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // must mean the user clicked Catch
        String trainerDiscordId = event.getMember().getId();
        String pokemonId = event.getButton().getId().split(":")[2];
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId);
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());
        if (pokemonController.catchPokemon(pokemon.getId())) {
            trainerController.addPokemonToTrainer(trainerDiscordId, pokemon);

            event.reply(
                            String.format(
                                    "Player <@%s> caught the powerful Pokemon of the day: %s",
                                    trainerDiscordId, species.getName().toUpperCase()))
                    .setEphemeral(true)
                    .queue();

        } else {
            event.reply(
                            String.format(
                                    "Missed catching %s! Try again tomorrow..",
                                    species.getName().toUpperCase()))
                    .setEphemeral(true)
                    .queue();
        }
    }
}
