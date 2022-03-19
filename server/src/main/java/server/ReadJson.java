package server;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import commons.Activity;

public class ReadJson {

    private List<Activity> activities = new ArrayList<>();
    public void readFile() throws InterruptedException{
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module =
                new SimpleModule("CustomActivityDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Activity.class, new CustomActivityDeserializer());
        mapper.registerModule(module);

        try {
            // normal size
            activities = mapper.readValue(new File(getClass().getResource("/activities/activities.json").toURI()),
                    new TypeReference<List<Activity>>(){});
        } catch(IOException | URISyntaxException e) {
            e.printStackTrace();
        }

//        for (Activity a : activities) {
//            System.out.println(a);
//        }
    }
}
