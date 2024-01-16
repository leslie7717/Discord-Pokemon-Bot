package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class LookUpCommandTest {

    @Test
    void testNameMatchesData() {
        LookUpCommand lookupCommand = new LookUpCommand();
        String name = lookupCommand.getName();
        CommandData commandData = lookupCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }
}
