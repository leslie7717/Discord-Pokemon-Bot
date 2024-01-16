package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Move;
import edu.northeastern.cs5500.starterbot.model.MoveEffectiveness;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MoveControllerTest {
    MoveController moveController = new MoveController();
    /**
     * Positive test for getting the move from json given the move name. If fails bug must be
     * controller. Test will not provide false positves or false negatives.
     */
    @Test
    void testLookUpMoveByName() {
        String moveName = "pound";
        Move result = moveController.lookUpMoveByName(moveName);
        assertThat(result).isNotNull();
        assertThat(PokemonType.NORMAL).isEqualTo(result.getType());
    }

    /**
     * Negative test for getting the move from json given invalid move name. If fails bug must be
     * controller or moves.json file. Test will not provide false positves or false negatives.
     */
    @Test
    void testMoveNotFound() {
        String moveName = "movename";
        Move result = moveController.lookUpMoveByName(moveName);
        assertThat(result).isNull();
    }

    /**
     * Positive test for getting the move effectiveness given the move name. If fails bug must be
     * move controller or PokemonType.getEffectivenessOffense() method. Test will not provide false
     * positves or false negatives.
     */
    @Test
    void testGetMoveEffectiveness() {
        String moveName = "pound";
        Map<PokemonType, MoveEffectiveness> result = moveController.getMoveEffectiveness(moveName);
        assertThat(result).isNotNull();
        assertThat(MoveEffectiveness.HALF_EFFECT).isEqualTo(result.get(PokemonType.ROCK));
    }

    /**
     * Negative test for getting the move effectiveness given invalid move name. If fails bug must
     * be controller or moves.json file. Test will not provide false positves or false negatives.
     */
    @Test
    void testGetMoveEffectivenessIsNull() {
        String moveName = "movename";
        Map<PokemonType, MoveEffectiveness> result = moveController.getMoveEffectiveness(moveName);
        assertThat(result).isNull();
    }

    @Test
    void testThatGetsEffectivenessOffense() {
        Map<PokemonType, MoveEffectiveness> map =
                moveController.getEffectivenessOffense(PokemonType.WATER);
        assertThat(map).isNotNull();
        assertThat(MoveEffectiveness.DOUBLE_EFFECT).isEqualTo(map.get(PokemonType.FIRE));
    }

    @Test
    void testThatGetsEffectivenessDefense() {
        List<PokemonType> pokeTypes = new ArrayList<>();
        pokeTypes.add(PokemonType.FLYING);
        pokeTypes.add(PokemonType.FIRE);

        Map<PokemonType, MoveEffectiveness> map = moveController.getEffectivenessDefense(pokeTypes);
        assertThat(map).isNotNull();
        assertThat(MoveEffectiveness.QUAD_EFFECT).isEqualTo(map.get(PokemonType.ROCK));
    }
}
