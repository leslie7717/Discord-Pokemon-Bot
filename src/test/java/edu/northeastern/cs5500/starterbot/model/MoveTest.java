package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.model.Move.MoveBuilder;
import org.junit.jupiter.api.Test;

public class MoveTest {
    @Test
    void testMoveCreationWithValidValues() {
        MoveBuilder moveBuilder = Move.builder();
        moveBuilder.moveId(1);
        moveBuilder.name("pound");
        moveBuilder.accuracy(10);
        moveBuilder.power(10);
        moveBuilder.pp(10);
        moveBuilder.type(PokemonType.NORMAL);
        moveBuilder.category(MoveCategory.PHYSICAL);
        Move move = moveBuilder.build();

        assertThat(move.getId()).isNotNull();
        assertThat(move.getMoveId()).isEqualTo(1);
        assertThat(move.getName()).isEqualTo("pound");
        assertThat(move.getAccuracy()).isEqualTo(10);
        assertThat(move.getPower()).isEqualTo(10);
        assertThat(move.getPp()).isEqualTo(10);
        assertThat(move.getType()).isEqualTo(PokemonType.NORMAL);
        assertThat(move.getCategory()).isEqualTo(MoveCategory.PHYSICAL);
    }

    @Test
    void testMoveCreationWithNullPP() {
        MoveBuilder moveBuilder = Move.builder();
        moveBuilder.moveId(1);
        moveBuilder.name("pound");
        moveBuilder.accuracy(10);
        moveBuilder.power(10);
        moveBuilder.pp(null);
        moveBuilder.type(PokemonType.NORMAL);
        moveBuilder.category(MoveCategory.PHYSICAL);

        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            moveBuilder.build();
                        });

        String expectedMessage = "pp must not be null";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void testMoveCreationWithNullType() {
        MoveBuilder moveBuilder = Move.builder();
        moveBuilder.moveId(1);
        moveBuilder.name("pound");
        moveBuilder.accuracy(10);
        moveBuilder.power(10);
        moveBuilder.pp(10);
        moveBuilder.type(null);
        moveBuilder.category(MoveCategory.PHYSICAL);

        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            moveBuilder.build();
                        });

        String expectedMessage = "type must not be null";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void testMoveCreationWithNullCategory() {
        MoveBuilder moveBuilder = Move.builder();
        moveBuilder.moveId(1);
        moveBuilder.name("pound");
        moveBuilder.accuracy(10);
        moveBuilder.pp(10);
        moveBuilder.type(PokemonType.NORMAL);
        moveBuilder.category(null);

        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            moveBuilder.build();
                        });

        String expectedMessage = "category must not be null";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
