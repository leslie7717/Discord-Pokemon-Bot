package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class AttackMoveEffectivenessCommandTest {

    @Test
    void testNameMatchesData() {
        AttackMoveEffectivenessCommand attackMoveEffectivenessCommand =
                new AttackMoveEffectivenessCommand();
        String name = attackMoveEffectivenessCommand.getName();
        CommandData commandData = attackMoveEffectivenessCommand.getCommandData();
        assertThat(name).isEqualTo(commandData.getName());
    }
}
