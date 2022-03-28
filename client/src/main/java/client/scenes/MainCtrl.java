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

import client.utils.BeforeLeave;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.ClientMessage;
import commons.ServerMessage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.UUID;

import static javafx.application.Platform.runLater;

public class MainCtrl {

    private final ServerUtils server;

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private Scene waitingRoom;
    private WaitingRoomScreenCtrl waitingRoomScreenCtrl;

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

    private Scene inputName;
    private InputNameScreenCtrl inputNameScreenCtrl;

    private String clientID = null;
    private String gameID = null;
    private String name = null;

    @Inject
    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<WaitingRoomScreenCtrl, Parent> waitingRoom,
                           Pair<SingleplayerLeaderboardCtrl, Parent> singleplayerLeaderboard,
                           Pair<MultiplayerScreenCtrl, Parent> multiplayer,
                           Pair<SplashScreenCtrl, Parent> splashScreen,
                           Pair<InBetweenScoreCtrl, Parent> inBetweenScore,
                           Pair<LeaveCtrl, Parent> leave,
                           Pair<SingleplayerScreenCtrl, Parent> singleplayerGame,
                           Pair<InputNameScreenCtrl, Parent> inputname){
        this.primaryStage = primaryStage;

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.waitingRoom = new Scene(waitingRoom.getValue());
        this.waitingRoomScreenCtrl = waitingRoom.getKey();

        this.singleplayerLeaderboardCtrl = singleplayerLeaderboard.getKey();
        this.singleLeaderboard = new Scene(singleplayerLeaderboard.getValue());

        this.multiplayerScreenCtrl = multiplayer.getKey();
        this.multiplayer = new Scene(multiplayer.getValue());

        this.splashScreenCtrl = splashScreen.getKey();
        this.splash = new Scene(splashScreen.getValue());

        this.inBetweenScoreCtrl = inBetweenScore.getKey();
        this.inBetweenScore = new Scene(inBetweenScore.getValue());

        this.leave = new Scene(leave.getValue());
        this.leaveCtrl = leave.getKey();

        this.singleplayerScreen = new Scene(singleplayerGame.getValue());
        this.singleplayerScreenCtrl = singleplayerGame.getKey();

        this.inputName = new Scene(inputname.getValue());
        this.inputNameScreenCtrl = inputname.getKey();

        showOverview();
        primaryStage.show();

        clientID = UUID.randomUUID().toString();
        server.registerForMessage("/topic/client/" + clientID, ServerMessage.class, this::handleServerMessage);
    }

    //CHECKSTYLE:OFF
    public void handleServerMessage(ServerMessage msg){

        switch(msg.type){
            case INIT_PLAYER:
                gameID = msg.gameID;
                runLater(() -> {
                    multiplayerScreenCtrl.updateScore(0);
                    showWaitingRoom();
                });
                break;
            case EXTRA_PLAYER:
                runLater(() -> {
//                    showWaitingRoom();
                    waitingRoomScreenCtrl.updatePlayerList(msg.playersWaiting);
                });
                break;
            case NEW_MULTIPLAYER_GAME:
                // do something
                break;
            case NEW_SINGLEPLAYER_GAME:
                gameID = msg.gameID;
                break;
            case LOAD_NEW_QUESTIONS:
                // runLater() must be used to run the following code
                // on the JavaFX Application Thread
                runLater(() -> {
                    multiplayerScreenCtrl.setTimer(msg.timerFraction, msg.timerFull);
                    multiplayerScreenCtrl.displayActivities(msg.question.getActivities());
                    showMultiplayerScreen();
                });
                System.out.println("[msg] loadingGame");
                break;
            case DISPLAY_ANSWER:
                runLater(() -> {
                    System.out.println("[update] topScores: " + msg.topScores);
                    multiplayerScreenCtrl.showAnswer(msg.correctID, msg.pickedID);
                    multiplayerScreenCtrl.updateScore(msg.score);
                    inBetweenScoreCtrl.setScoreTo(msg.score);
                    inBetweenScoreCtrl.insertLeaderboard(msg.topScores);
                });
                System.out.println("[msg] display answer");

                break;
            case DISPLAY_INBETWEENSCORES:
                runLater(() -> {
                    multiplayerScreenCtrl.updateTitle(msg.questionCounter);
                    inBetweenScoreCtrl.setQuestionNo(msg.questionCounter, msg.totalQuestions);
                    showInbetweenScore();
                });
                System.out.println("[msg] show leaderboard ");
                break;
            case END_GAME:
                gameID = msg.gameID;
                runLater(this::showWaitingRoom);
                System.out.println("[msg] end game");
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
                break;
            case TEST:
                // for testing purposes only
                System.out.println("[test] message received");
                break;
            default:
                // invalid msg type
        }
    }
    //CHECKSTYLE:ON

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showInbetweenScore() {
        primaryStage.setTitle("Score");
        primaryStage.setScene(inBetweenScore);
    }

    public void showLeave(Scene scene){
        leaveCtrl.setPrevious(scene);
        primaryStage.setScene(leave);
    }

    public void showLeave(Scene scene, BeforeLeave beforeLeave){
        leaveCtrl.setPrevious(scene);
        leaveCtrl.setBeforeLeave(beforeLeave);
        primaryStage.setScene(leave);
    }

    public void stay(Scene previous){
        primaryStage.setScene(previous);
    }

    public void showAdd() {
        // For testing only: send a test message to the server
        server.send("/app/general", new ClientMessage(ClientMessage.Type.TEST, clientID, "0"));
        System.out.println("DID sth");

        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
    }

    public void showSplash(){
        primaryStage.setTitle("SplashScreen");
        primaryStage.setScene(splash);
    }

    public void showMultiplayerScreen(){
        primaryStage.setTitle("MultiplayerScreen");
        primaryStage.setScene(multiplayer);

    }

    public void showSingleLeaderboardScreen(){
        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(singleLeaderboard);
    }

    public void showWaitingRoom() {
        primaryStage.setTitle("WaitingRoomScreen");
        primaryStage.setScene(waitingRoom);
    }

    public void showinputNameScreen() {
        primaryStage.setTitle("input Name");
        primaryStage.setScene(inputName);
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

    public Scene getOverview() {
        return overview;
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

    public Scene getSingleplayerScreen() {
        return singleplayerScreen;
    }

    public Scene getInputName() {
        return inputName;
    }

    public String getClientID() {
        return clientID;
    }

    public String getGameID() {
        return gameID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}