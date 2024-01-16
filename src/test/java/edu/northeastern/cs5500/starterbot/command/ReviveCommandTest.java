package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class ReviveCommandTest {

    @Test
    void testNameMatchesData() {
        ReviveCommand reviveCommand = new ReviveCommand();
        String name = reviveCommand.getName();
        CommandData commandData = reviveCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }
}
