/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import commons.Activity;
import commons.Score;
import jakarta.ws.rs.client.Entity;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    //private static final String SERVER = "http://localhost:8080/";
//    private String server = "http://localhost:8080/";
    private String server = null;
    //TODO: change this once other pages' initialize is changed
    //TODO: make it so that it gets connected to the server specified by the player

    private StompSession session;
    //TODO: change this once other pages' initialize is changed

    public void setServerURL(String server){
        this.server = server;
    }

    public String getServerURL(){ return this.server; }

    public boolean isConnected(){
        if(server == null || session == null) return false;

        return session.isConnected();
    }

    public ServerUtils() {
        System.out.println("CONSTRUCTED");
    }

    public List<Score> getTopScores(){
        List<Score> scores = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/scores/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Score>>() {});

        scores.sort(Comparator.comparingInt(Score::getPlayerScore));
        Collections.reverse(scores);
        return scores;
    }

    public String getClientID(){
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/util/getPlayerID")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(String.class);
    }

    /**
     * Creates the url for working with websockets
     * @return returns the url for websockets
     */
    private String getWebsocketServerName() {
        String websocketServerName= server.replaceAll("http", "ws");
        return websocketServerName + "websocket";
    }

    public List<Activity> getActivities(){
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/activities/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Activity>>() {});
    }

    public void addActivity(@RequestBody Activity newActivity){
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/activities/")
                .request(APPLICATION_JSON)
                .put(Entity.json(newActivity));
    }

    public void editActivity(@PathVariable long id, @RequestBody Activity newActivity){
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/activities/edit/" + id)
                .request(APPLICATION_JSON)
                .put(Entity.json(newActivity));
    }

    public void removeActivity(@PathVariable long id){
        ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/activities/" + id)
                .request(APPLICATION_JSON)
                .delete();
    }

    private StompSession connect(String url){
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());

        try{
            return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
        }
        catch(ExecutionException e){
            throw new RuntimeException(e);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }

        throw new IllegalStateException();
    }

    public <T> void registerForMessage(String dest, Class<T> type, Consumer<T> consumer){
        // set up the connection
        session.subscribe(dest, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type; // we expect to get something of type 'type'
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // we know that 'payload' will be of type T, because Jackson Converter
                // takes care of that
                consumer.accept((T) payload);
            }
        });
    }

    public void send(String dest, Object o){ session.send(dest, o); }

    public boolean reconnect(){
        if(session != null && session.isConnected()) session.disconnect();

        try{
            session = connect(getWebsocketServerName());
        }
        catch(Exception e){
            System.out.println("Exception on WebSocket connect(): " + e.getMessage());
            session = null;
            return false;
        }

        return session.isConnected();
    }
}