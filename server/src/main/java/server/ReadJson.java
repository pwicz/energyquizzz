package server;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import commons.Activity;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

@Service
public class ReadJson {

    private List<Activity> activities = new ArrayList<>();
    private final ActivityRepository repo;

    public ReadJson(ActivityRepository repo) {
        this.repo = repo;
    }

    public void readFile(){
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module =
                new SimpleModule("CustomActivityDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Activity.class, new CustomActivityDeserializer());
        mapper.registerModule(module);

        try {
            // Activities should be placed in public/activities so that their images are accessible
            // through paths such as localhost:8080/activities/20/dryer.jpg
            activities = mapper.readValue(
                             new File(Objects.requireNonNull(getClass()
                                             .getResource("/public/activities/activities.json"))
                            .toURI()),
                    new TypeReference<>(){});
        } catch(IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void saveAllToDB(){
        repo.saveAll(activities);
    }

    public void saveSomeToDB(int numberOfActivitiesToSave){
        for(int i = 0; i < numberOfActivitiesToSave; ++i) repo.save(activities.get(i));
    }
}
