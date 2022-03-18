package server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import commons.Activity;

public class ReadJson {

    private List<Activity> activities= new ArrayList<>();
    public void readFile() throws InterruptedException{
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module =
                new SimpleModule("CustomCarDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Activity.class, new CustomActivityDeserialiser());
        mapper.registerModule(module);
        try {
                activities = mapper.readValue(new File("server/src/main/java/server/package.json"), new TypeReference<List<Activity>>(){});
        }catch(IOException e) {
            e.printStackTrace();
        }

        for (Activity a : activities) {
            System.out.println(a);
        }
    }




}
