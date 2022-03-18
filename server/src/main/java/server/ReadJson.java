package server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import commons.Activity;

public class ReadJson {

    private List<Activity> activities= new ArrayList<>();
    public void readFile() throws InterruptedException{
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module =
                new SimpleModule("CustomActivityDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Activity.class, new CustomActivityDeserializer());
        mapper.registerModule(module);

        try {
            //uncomment for testing small size
            activities = mapper.readValue(new File("server/src/main/java/server/test.json"),
                    new TypeReference<List<Activity>>(){});
            //uncomment for real game big size
//            activities = mapper.readValue(new File("server/src/main/java/server/activities.json"),
//            new TypeReference<List<Activity>>(){});

        }catch(IOException e) {
            e.printStackTrace();
        }

        for (Activity a : activities) {
            System.out.println(a);
        }
    }
}
