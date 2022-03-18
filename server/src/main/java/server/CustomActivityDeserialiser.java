package server;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import commons.Activity;

import java.io.IOException;

public class CustomActivityDeserialiser extends StdDeserializer<Activity> {

    public CustomActivityDeserialiser() {
        this(null);
    }

    public CustomActivityDeserialiser(Class<Activity> a) {
        super(a);
    }

    @Override
    public Activity deserialize
            (JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        Activity activity = new Activity();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        activity.imagePath = node.get("image_path").asText();
        activity.title = node.get("title").asText();
        activity.consumptionInWh = node.get("consumption_in_wh").asInt();
        activity.source = node.get("source").asText();

        return activity;
    }
}
