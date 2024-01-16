package edu.northeastern.cs5500.starterbot.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.starterbot.model.Move;
import edu.northeastern.cs5500.starterbot.model.Move.MoveBuilder;
import edu.northeastern.cs5500.starterbot.model.MoveCategory;
import edu.northeastern.cs5500.starterbot.model.MoveEffectiveness;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MoveController {

    @Inject
    public MoveController() {}

    /** Finds move info from moves.json and returns move object */
    public Move lookUpMoveByName(@Nonnull String moveName) {
        JsonNode moves = getResourcesForMove();
        if (moves.has(moveName.toLowerCase())) {
            JsonNode moveNode = moves.get(moveName.toLowerCase());
            MoveBuilder builder = Move.builder();
            builder.moveId(moveNode.at("/id").asInt());
            builder.name(moveName.toLowerCase());
            builder.accuracy(moveNode.at("/accuracy").asInt());
            builder.power(moveNode.at("/power").asInt());
            builder.pp(moveNode.at("/pp").asInt());
            builder.type(PokemonType.valueOf(moveNode.at("/type").asText().toUpperCase()));
            builder.category(MoveCategory.valueOf(moveNode.at("/category").asText().toUpperCase()));
            return builder.build();
        }
        return null;
    }

    /**
     * Computes defense effectiveness for each Pokemon Type given the trainer's pokemon type
     *
     * @param attackType trainer's pokemon type
     * @return hashamp of all PokemonType and their move effect against trainer's pokemon type
     */
    public Map<PokemonType, MoveEffectiveness> getEffectivenessOffense(PokemonType attackType) {
        Map<String, Map<String, Double>> moveEffectiveness = getResourcesForMoveEffectiveness();
        Map<String, Double> defenseTypes = moveEffectiveness.get(attackType.getName());

        Map<PokemonType, MoveEffectiveness> effects = new HashMap<>();

        for (Entry<String, Double> defenseType : defenseTypes.entrySet()) {
            PokemonType def = PokemonType.valueOf(defenseType.getKey().toUpperCase());
            MoveEffectiveness effect = MoveEffectiveness.getEffectiveness(defenseType.getValue());
            effects.put(def, effect);
        }

        return effects;
    }
    /**
     * return move effectiveness of each Pokemon Type given opponent's pokemon types
     *
     * @param attackTypes list of opponents pokemon types
     * @return hashamp of all PokemonType and their move effect that trainer pokemon type will take.
     */
    public Map<PokemonType, MoveEffectiveness> getEffectivenessDefense(
            List<PokemonType> attackTypes) {
        Map<String, Map<String, Double>> moveEffectiveness = getResourcesForMoveEffectiveness();
        Map<PokemonType, MoveEffectiveness> effects = new HashMap<>();

        for (Map.Entry<String, Map<String, Double>> attackType : moveEffectiveness.entrySet()) {
            PokemonType def = PokemonType.valueOf(attackType.getKey().toUpperCase());
            Double effect = attackType.getValue().get(attackTypes.get(0).getName());
            if (attackTypes.size() > 1) {
                effect = effect * attackType.getValue().get(attackTypes.get(1).getName());
            }
            MoveEffectiveness eff = MoveEffectiveness.getEffectiveness(effect);
            effects.put(def, eff);
        }
        return effects;
    }

    /**
     * Computes move effectiveness of attack move for all Pokemon types.
     *
     * @param moveName attacker move name
     * @return move effectivess for all Pokemon types
     */
    public Map<PokemonType, MoveEffectiveness> getMoveEffectiveness(@Nonnull String moveName) {
        Move move = lookUpMoveByName(moveName);
        if (move != null) {
            return getEffectivenessOffense(move.getType());
        }
        return null;
    }

    /**
     * Fetches moves_effectiveness json and convert it to JsonNode
     *
     * @returns JsonNode
     */
    private JsonNode getResourcesForMove() {
        InputStream stream = this.getClass().getResourceAsStream("/moves.json");
        try {
            byte[] moves = stream.readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(moves);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Fetches movejson resource and convert it to map
     *
     * @return Map<String, Map<String, Double>> Pokemon.json as map
     */
    private Map<String, Map<String, Double>> getResourcesForMoveEffectiveness() {
        InputStream stream = this.getClass().getResourceAsStream("/move_effectiveness.json");
        try {
            byte[] moveEffectivenessData = stream.readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    moveEffectivenessData,
                    new TypeReference<Map<String, Map<String, Double>>>() {});
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Takes a Pokemon type and the map of moves, and returns a random move of that type
     *
     * @param type - a specific type
     * @param movesByType
     * @return a random move based on the specific type
     */
    public Move getRandomMove(String type, Map<String, List<Move>> movesByType) {
        List<Move> moves = movesByType.get(type);
        if (moves == null || moves.isEmpty()) {
            return null; // or some default move
        }
        Random rand = new Random();
        return moves.get(rand.nextInt(moves.size()));
    }

    public Map<String, List<Move>> loadMovesData() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode moves = getResourcesForMove();

        Map<String, Map<String, Object>> movesMap =
                objectMapper.convertValue(
                        moves, new TypeReference<Map<String, Map<String, Object>>>() {});

        Map<String, List<Move>> movesByType = new HashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : movesMap.entrySet()) {
            String moveName = entry.getKey();
            Map<String, Object> moveData = entry.getValue();

            // Create a new Move object and set its properties
            Move move =
                    Move.builder()
                            .name(moveName)
                            .moveId((Integer) moveData.get("id"))
                            .accuracy((Integer) moveData.get("accuracy"))
                            .power((Integer) moveData.get("power"))
                            .pp((Integer) moveData.get("pp"))
                            .type(PokemonType.fromString((String) moveData.get("type")))
                            .category(MoveCategory.fromString((String) moveData.get("category")))
                            .build();

            String typeKey = move.getType().getName();
            movesByType.computeIfAbsent(typeKey, k -> new ArrayList<>()).add(move);
        }
        return movesByType;
    }
}
