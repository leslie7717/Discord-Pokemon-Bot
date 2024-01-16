package edu.northeastern.cs5500.starterbot.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Trainer implements Model {
    private ObjectId id;

    // This is the "snowflake id" of the user
    // e.g. event.getUser().getId()
    private String discordUserId;

    private List<ObjectId> pokemonCollection = new ArrayList<>();

    private String pokemonOfDayTimeStamp;
}
