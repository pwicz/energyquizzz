package server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import commons.Question; //future activity

public class ReadJsonFile {
    //List<Activity> in the future
    private List<Question> activities= new ArrayList<>(); //activity
    public void main(String[] args) throws InterruptedException{

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            activities = mapper.readValue(new File("package.json"), new TypeReference<List<Question>>(){});//activity
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


}