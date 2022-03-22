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
package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.ServerMessage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import static javafx.application.Platform.runLater;

public class MainCtrl {

    private final ServerUtils server;

    private Stage primaryStage;

    private Scene waitingRoom;

    private SplashScreenCtrl splashScreenCtrl;
    private Scene splash;

    private Scene multiplayer;
    private  MultiplayerScreenCtrl multiplayerScreenCtrl;

    private Scene singleLeaderboard;
    private SingleplayerLeaderboardCtrl singleplayerLeaderboardCtrl;

    private Scene inBetweenScore;
    private InBetweenScoreCtrl inBetweenScoreCtrl;

    private Scene leave;
    private LeaveCtrl leaveCtrl;

    private Scene singleplayerScreen;
    private SingleplayerScreenCtrl singleplayerScreenCtrl;

    private String clientID = null;
    private String gameID = null;
    private int score;

    @Inject
    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    public void initialize(Stage primaryStage, Pair<SplashScreenCtrl, Parent> splashScreen,
                           Pair<SingleplayerLeaderboardCtrl, Parent> singleplayerLeaderboard,
                           Pair<SingleplayerScreenCtrl, Parent> singleplayerGame,
                           Pair<WaitingRoomScreenCtrl, Parent> waitingRoom,
                           Pair<MultiplayerScreenCtrl, Parent> multiplayer,
                           Pair<InBetweenScoreCtrl, Parent> inBetweenScore,
                           Pair<LeaveCtrl, Parent> leave
                           ){
        this.primaryStage = primaryStage;

        this.splashScreenCtrl = splashScreen.getKey();
        this.splash = new Scene(splashScreen.getValue());

        this.singleplayerLeaderboardCtrl = singleplayerLeaderboard.getKey();
        this.singleLeaderboard = new Scene(singleplayerLeaderboard.getValue());

        this.singleplayerScreen = new Scene(singleplayerGame.getValue());
        this.singleplayerScreenCtrl = singleplayerGame.getKey();

        this.waitingRoom = new Scene(waitingRoom.getValue());

        this.multiplayerScreenCtrl = multiplayer.getKey();
        this.multiplayer = new Scene(multiplayer.getValue());

        this.inBetweenScoreCtrl = inBetweenScore.getKey();
        this.inBetweenScore = new Scene(inBetweenScore.getValue());

        this.leave = new Scene(leave.getValue());
        this.leaveCtrl = leave.getKey();

        showSplash();
        primaryStage.show();

        clientID = "233"; // hardcoded: we need to somehow get it from the server

        server.registerForMessage("/topic/client/" + clientID, ServerMessage.class, this::handleServerMessage);
    }

    public void handleServerMessage(ServerMessage msg){
        switch(msg.type){
            case NEW_MULTIPLAYER_GAME:
                // do something
                break;
            case NEW_SINGLEPLAYER_GAME:
                gameID = msg.gameID;
                break;
            case NEXT_QUESTION:
                // runLater() must be used to run the following code
                // on the JavaFX Application Thread
                runLater(() -> {
                    singleplayerScreenCtrl.restoreView();
                    singleplayerScreenCtrl.displayActivities(msg.question.activities);
                    singleplayerScreenCtrl.setScoreTo(msg.score);
                    singleplayerScreenCtrl.setTitleTo("Question " + msg.round + ": " + msg.question.title);
                    singleplayerScreenCtrl.setTimer(msg.timerFraction, msg.timerFull);
                    showSingleplayerGameScreen();
                });
                break;
            case RESULT:
                long correctID = msg.correctAnswerID;
                long pickedID = msg.pickedAnswerID;
                runLater(() -> {
                    singleplayerScreenCtrl.setScoreTo(msg.score);
                    singleplayerScreenCtrl.showAnswer(correctID, pickedID);
                });
                break;
            case END:
                runLater(this::showSingleLeaderboardScreen);
            case TEST:
                // for testing purposes only
                System.out.println("It works! Received a msg!");
                break;
            default:
                // invalid msg type
        }
    }

    public void showInbetweenScore() {
        primaryStage.setTitle("Score");
        primaryStage.setScene(inBetweenScore);
    }

    public void showLeave(Scene scene){
        leaveCtrl.setPrevious(scene);
        primaryStage.setScene(leave);
    }

    public void stay(Scene previous){
        primaryStage.setScene(previous);
    }

    public void showSplash(){
        primaryStage.setTitle("SplashScreen");
        primaryStage.setScene(splash);
    }

    public void showMultiplayerScreen(){
        primaryStage.setTitle("Multiplayer");
        primaryStage.setScene(multiplayer);
        multiplayerScreenCtrl.decreaseTime();
    }

    public void showSingleLeaderboardScreen(){
        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(singleLeaderboard);
    }

    public void showWaitingRoom() {
        primaryStage.setTitle("WaitingRoomScreen");
        primaryStage.setScene(waitingRoom);
    }

    public void showSingleplayerGameScreen(){
        primaryStage.setTitle("Singleplayer");
        primaryStage.setScene(singleplayerScreen);
    }
    public Scene getInBetweenScore() {
        return inBetweenScore;
    }

    public Scene getMultiplayer() {
        return multiplayer;
    }

    public Scene getLeave() {
        return leave;
    }


    public Scene getSingleLeaderboard() {
        return singleLeaderboard;
    }

    public Scene getSplash() {
        return splash;
    }

    public Scene getWaitingRoom() {
        return waitingRoom;
    }

    public String getClientID() {
        return clientID;
    }

    public String getGameID() {
        return gameID;
    }

}