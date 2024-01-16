package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/*
 * This class is responsible for testing Pokemon model class.
 *
 */
class PokemonTest {
    @Test
    void testPokemonCreationWithValidValues() {
        Pokemon pokemon = new Pokemon(1, 10, 10, 100, 15, 10, 20, 25, 5, null, null);

        assertThat(pokemon.getId()).isNotNull();
        assertThat(pokemon.getPokedexNumber()).isEqualTo(1);
        assertThat(pokemon.getLevel()).isEqualTo(5);
        assertThat(pokemon.getCurrentHp()).isEqualTo(10);
        assertThat(pokemon.getHp()).isEqualTo(10);
        assertThat(pokemon.getXp()).isEqualTo(100);
        assertThat(pokemon.getAttack()).isEqualTo(15);
        assertThat(pokemon.getDefense()).isEqualTo(10);
        assertThat(pokemon.getSpecialAttack()).isEqualTo(20);
        assertThat(pokemon.getSpecialDefense()).isEqualTo(25);
        assertThat(pokemon.getSpeed()).isEqualTo(5);
    }

    @Test
    void testPokemonCreationWithNegativeCurrentHp() {
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            new Pokemon(1, -10, 10, 100, 15, 10, 20, 25, 5, null, null);
                        });

        String expectedMessage = "currentHp must be non-null and non-negative";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void testPokemonCreationWithNullPokedexNumber() {
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            new Pokemon(null, 10, 10, 100, 15, 10, 20, 25, 5, null, null);
                        });

        String expectedMessage = "pokedexNumber must not be null";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
