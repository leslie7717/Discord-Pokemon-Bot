package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TradeController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.Trade;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bson.types.ObjectId;

@Slf4j
public class TradeCommand implements SlashCommandHandler, ButtonHandler {
    static final String NAME = "trade";

    @Inject PokemonController pokemonController;

    @Inject PokedexController pokedexController;

    @Inject TrainerController trainerController;

    @Inject TradeController tradeController;

    @Inject
    public TradeCommand() {
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
        return Commands.slash(getName(), "Trade pokemon with other trainers")
                .addOption(OptionType.STRING, "offer", "Pokemon name you want to offer", true)
                .addOption(OptionType.STRING, "request", "Pokemon name you want to request", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /trade");

        var offer = event.getOption("offer");
        var request = event.getOption("request");

        if (offer == null || request == null) {
            log.error("Received null value for mandatory parameters");
            return;
        }

        // Checks if given pokemons exist
        Pokemon offerPokemon = getPokemonByName(event, offer.getAsString());
        Pokemon requestPokemon = getPokemonByName(event, request.getAsString());
        if (offerPokemon == null || requestPokemon == null) {
            return;
        }

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();

        // Creates the trade offering. IllegalArgument exception thrown if the trainer does not have
        // the offered pokemon in pokedex.
        try {
            Trade trade =
                    tradeController.createNewOffering(
                            trainerController.getTrainerForMemberId(event.getMember().getId()),
                            offerPokemon.getPokedexNumber(),
                            requestPokemon.getPokedexNumber());

            // Send trade object id in the button
            messageCreateBuilder =
                    messageCreateBuilder.addActionRow(
                            Button.primary(getName() + ":" + trade.getId(), "Trade"));
        } catch (IllegalArgumentException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trade Proposal");
        embedBuilder.addField(
                "Offer",
                getPokemonEmbed(offerPokemon)
                        .append("Level:")
                        .append(offerPokemon.getLevel())
                        .toString(),
                false);

        embedBuilder.addField("Request", getPokemonEmbed(requestPokemon).toString(), false);
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // must mean the user clicked Trade

        // extract trade id from the button
        String tradeId = event.getButton().getId().split(":")[1];
        String requestTrainer = event.getMember().getId();

        // Initiate the trade acceptance.
        // throws IllegalArgumentException if the trainer does not have the requested pokemon in
        // pokedex.
        // throws IllegalStateException if the trade is already closed
        try {
            tradeController.acceptOffering(
                    trainerController.getTrainerForMemberId(requestTrainer), new ObjectId(tradeId));
        } catch (IllegalArgumentException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return;
        } catch (IllegalStateException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
            return;
        }

        event.reply("Trade successful").setEphemeral(true).queue();
    }

    /**
     * Retrieves Pokemon information based on the provided Pokemon name.
     *
     * @param event slash command event
     * @param pokemonName name of the pokemon
     * @return The Pokemon object if found; otherwise, null.
     */
    private Pokemon getPokemonByName(SlashCommandInteractionEvent event, String pokemonName) {
        Pokemon pokemon = pokemonController.lookUpPokemonInfoByName(pokemonName);
        if (pokemon == null) {
            event.reply(String.format("%s species cannot be found!", pokemonName))
                    .setEphemeral(true)
                    .queue();
            return null;
        }
        return pokemon;
    }

    /**
     * Retrieves an embed content for a Pokemon, including its name and types.
     *
     * @param pokemon
     * @return StringBuilder containing the formatted embed content.
     */
    private StringBuilder getPokemonEmbed(Pokemon pokemon) {
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());
        StringBuilder content = new StringBuilder();

        content.append(species.getName())
                .append(species.getTypes().get(0).getEmoji())
                .append(species.getTypes().size() > 1 ? species.getTypes().get(1).getEmoji() : "")
                .append("\n");
        return content;
    }
}
