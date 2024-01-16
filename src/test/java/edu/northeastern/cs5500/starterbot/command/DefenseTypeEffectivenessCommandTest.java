package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

public class DefenseTypeEffectivenessCommandTest {
    @Test
    void testNameMatchesData() {
        DefenseTypeEffectivenessCommand defenseTypeEffectivenessCommand =
                new DefenseTypeEffectivenessCommand();
        String name = defenseTypeEffectivenessCommand.getName();
        CommandData commandData = defenseTypeEffectivenessCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }
}
