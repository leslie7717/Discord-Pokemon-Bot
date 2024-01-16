package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

public class PokemonOfTheDayCommandTest {

    @Test
    void testNameMatchesData() {
        PokemonOfDayCommand pokemonOfDayCommand = new PokemonOfDayCommand();
        String name = pokemonOfDayCommand.getName();
        CommandData commandData = pokemonOfDayCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }
}
