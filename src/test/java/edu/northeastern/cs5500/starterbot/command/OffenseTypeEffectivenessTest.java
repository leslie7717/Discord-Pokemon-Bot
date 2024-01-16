package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

public class OffenseTypeEffectivenessTest {

    @Test
    void testNameMatchesData() {
        OffenseTypeEffectivenessCommand offenseTypeEffectivenessCommand =
                new OffenseTypeEffectivenessCommand();
        String name = offenseTypeEffectivenessCommand.getName();
        CommandData commandData = offenseTypeEffectivenessCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }
}
