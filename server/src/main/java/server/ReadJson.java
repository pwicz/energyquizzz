package server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import commons.Activity;

public class ReadJson {

    private List<Activity> activities= new ArrayList<>();
    public void readFile() throws InterruptedException{

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            activities = mapper.readValue(new File("server/src/main/java/server/package.json"), new TypeReference<List<Activity>>(){});
        }catch(IOException e) {
            e.printStackTrace();
        }
    }




}
