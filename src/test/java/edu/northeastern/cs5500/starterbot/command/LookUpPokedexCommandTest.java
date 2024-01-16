package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class LookUpPokedexCommandTest {
    @Test
    void testNameMatchesData() {
        LookUpPokedexCommand lookUpPokedexCommand = new LookUpPokedexCommand();
        String name = lookUpPokedexCommand.getName();
        CommandData commandData = lookUpPokedexCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
