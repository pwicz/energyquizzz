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

import client.utils.OnLeaveAction;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.ClientMessage;
import commons.Question;
import commons.ServerMessage;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import static javafx.application.Platform.runLater;

public class MainCtrl {

    private final ServerUtils server;

    private Stage primaryStage;

    private Scene waitingRoom;
    private WaitingRoomScreenCtrl waitingRoomScreenCtrl;

    private SplashScreenCtrl splashScreenCtrl;
    private Scene splash;

    private Scene multiplayer;
    private Scene guessQuestionM;
    private Scene inputQuestionM;

    private  MultiplayerScreenCtrl multiplayerScreenCtrl;
    private  MultiplayerScreenCtrl multiplayerScreenGuessCtrl;
    private  MultiplayerScreenCtrl multiplayerScreenInputCtrl;

    private Scene singleLeaderboard;
    private SingleplayerLeaderboardCtrl singleplayerLeaderboardCtrl;

    private Scene inBetweenScore;
    private InBetweenScoreCtrl inBetweenScoreCtrl;

    private Scene leave;
    private LeaveCtrl leaveCtrl;

    private Scene singleplayerScreen;
    private Scene singleplayerInputScreen;
    private Scene singleplayerGuessScreen;

    private SingleplayerScreenCtrl singleplayerScreenCtrl;
    private SingleplayerScreenCtrl singleplayerScreenInputCtrl;
    private SingleplayerScreenCtrl singleplayerScreenGuessCtrl;

    private Scene adminPanel;
    private AdminPanelCtrl adminPanelCtrl;

    private Scene editActivity;
    private EditActivityCtrl editActivityCtrl;

    private Scene createActivity;
    private CreateActivityCtrl createActivityCtrl;

    private Scene inputName;
    private InputNameScreenCtrl inputNameScreenCtrl;

    private Scene inputServer;
    private InputServerScreenCtrl inputServerScreenCtrl;

    private Scene help;
    private HelpCtrl helpCtrl;

    private String clientID = null;
    private String gameID = null;

    private Stage stage = new Stage();


    @Inject
    public MainCtrl(ServerUtils server) {
        this.server = server;
    }

    public void initialize(Stage primaryStage, Pair<SplashScreenCtrl, Parent> splashScreen,
                           Pair<AdminPanelCtrl, Parent> adminPanel,
                           Pair<EditActivityCtrl, Parent> editActivity,
                           Pair<CreateActivityCtrl, Parent> createActivity,
                           Pair<SingleplayerLeaderboardCtrl, Parent> singleplayerLeaderboard,
                           Pair<SingleplayerScreenCtrl, Parent> singleplayerGame,
                           Pair<WaitingRoomScreenCtrl, Parent> waitingRoom,
                           Pair<MultiplayerScreenCtrl, Parent> multiplayer,
                           Pair<InBetweenScoreCtrl, Parent> inBetweenScore,
                           Pair<LeaveCtrl, Parent> leave,
                           Pair<InputNameScreenCtrl, Parent> inputName,
                           Pair<InputServerScreenCtrl, Parent> inputServer,
                           Pair<HelpCtrl, Parent> help,
                           Pair<MultiplayerScreenCtrl, Parent> inputQuestion,
                           Pair<MultiplayerScreenCtrl, Parent> guesQuestion,
                           Pair<SingleplayerScreenCtrl, Parent> inputQuestionS,
                           Pair<SingleplayerScreenCtrl, Parent> guesQuestionS){
        this.primaryStage = primaryStage;

        this.splashScreenCtrl = splashScreen.getKey();
        this.splash = new Scene(splashScreen.getValue());

        this.adminPanelCtrl = adminPanel.getKey();
        this.adminPanel = new Scene(adminPanel.getValue());

        this.editActivityCtrl = editActivity.getKey();
        this.editActivity = new Scene(editActivity.getValue());

        this.createActivityCtrl = createActivity.getKey();
        this.createActivity = new Scene(createActivity.getValue());

        this.waitingRoom = new Scene(waitingRoom.getValue());
        this.waitingRoomScreenCtrl = waitingRoom.getKey();

        this.singleplayerLeaderboardCtrl = singleplayerLeaderboard.getKey();
        this.singleLeaderboard = new Scene(singleplayerLeaderboard.getValue());

        this.singleplayerScreen = new Scene(singleplayerGame.getValue());
        this.singleplayerScreenCtrl = singleplayerGame.getKey();

        this.singleplayerGuessScreen = new Scene(guesQuestionS.getValue());
        this.singleplayerScreenGuessCtrl = guesQuestionS.getKey();

        this.singleplayerScreenInputCtrl = inputQuestionS.getKey();
        this.singleplayerInputScreen = new Scene(inputQuestionS.getValue());

        this.multiplayerScreenCtrl = multiplayer.getKey();
        this.multiplayer = new Scene(multiplayer.getValue());

        this.inputQuestionM = new Scene(inputQuestion.getValue());
        this.multiplayerScreenInputCtrl = inputQuestion.getKey();

        this.guessQuestionM = new Scene(guesQuestion.getValue());
        this.multiplayerScreenGuessCtrl = guesQuestion.getKey();

        this.inBetweenScoreCtrl = inBetweenScore.getKey();
        this.inBetweenScore = new Scene(inBetweenScore.getValue());

        this.inputName = new Scene(inputName.getValue());
        this.inputNameScreenCtrl = inputName.getKey();

        this.leave = new Scene(leave.getValue());
        this.leaveCtrl = leave.getKey();

        this.inputServer = new Scene(inputServer.getValue());
        this.inputServerScreenCtrl = inputServer.getKey();

        this.help = new Scene(help.getValue());
        this.helpCtrl = help.getKey();

        primaryStage.setResizable(false);
        showSplash();
        primaryStage.show();
        inputServerScreenCtrl.hideLeaveButton();
        showInputServer();
    }

    //CHECKSTYLE:OFF
    public void handleServerMessage(ServerMessage msg){

        switch(msg.type){
            case INIT_PLAYER:
                gameID = msg.gameID;
                runLater(() -> {
                    multiplayerScreenCtrl.updateScore(0);
                    multiplayerScreenGuessCtrl.updateScore(0);
                    multiplayerScreenInputCtrl.updateScore(0);
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
                primaryStage.setOnCloseRequest(e -> {
                    server.send("/app/general", new ClientMessage(ClientMessage.Type.QUIT, clientID, gameID));
                    Platform.exit();
                    System.exit(0);
                });
                break;
            case LOAD_NEW_QUESTIONS:
                // runLater() must be used to run the following code
                // on the JavaFX Application Thread
                runLater(() -> {
                    showQuestionM(msg);

                });
                System.out.println("[msg] loadingGame");
                break;
            case DISPLAY_ANSWER:
                runLater(() -> {
                    if(msg.typeQ == Question.Type.ESTIMATION){
                        multiplayerScreenInputCtrl.showAnswerInput(msg.answeredCorrect,
                                msg.correctID, msg.pickedID, msg.receivedPoints);
                    }else {
                        multiplayerScreenCtrl.showAnswer(msg.correctID, msg.pickedID);
                        multiplayerScreenGuessCtrl.showAnswer(msg.correctID, msg.pickedID);
                    }
                    System.out.println("[update] topScores: " + msg.topScores);
                    multiplayerScreenCtrl.updateScore(msg.score);
                    multiplayerScreenGuessCtrl.updateScore(msg.score);
                    multiplayerScreenInputCtrl.updateScore(msg.score);
                    inBetweenScoreCtrl.setScoreTo(msg.score);
                    inBetweenScoreCtrl.insertLeaderboard(msg.topScores);
                    inBetweenScoreCtrl.insertLeaderboardG(msg.correctlyAnswered);
                    inBetweenScoreCtrl.insertLeaderboardR(msg.incorrectlyAnswered);
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
                runLater(this::showWaitingRoom);
                System.out.println("[msg] end game");
                break;
            case NEXT_QUESTION:
                // runLater() must be used to run the following code
                // on the JavaFX Application Thread
                runLater(() -> {
                    showQuestionS(msg);

                });
                break;
            case RESULT:
                long correctID = msg.correctID;
                long pickedID = msg.pickedID;
                runLater(() -> {
                    if(msg.typeQ == Question.Type.ESTIMATION){
                        singleplayerScreenInputCtrl.showAnswerInput(msg.answeredCorrect,
                                correctID, pickedID, msg.receivedPoints);
                    }else{
                        singleplayerScreenGuessCtrl.showAnswer(correctID, pickedID);
                        singleplayerScreenCtrl.showAnswer(correctID, pickedID);
                    }
                    singleplayerScreenInputCtrl.setScoreTo(msg.score);
                    singleplayerScreenGuessCtrl.setScoreTo(msg.score);
                    singleplayerScreenCtrl.setScoreTo(msg.score);
                });
                break;
            case END:
                runLater(this::showSingleLeaderboardScreen);
            case TEST:
                // for testing purposes only
                System.out.println("It works! Received a msg!");
                break;
            case SHOW_EMOJI:
                runLater(() -> {
                    multiplayerScreenCtrl.showEmoji(msg.imgName, msg.namePLayerEmoji);
                    multiplayerScreenInputCtrl.showEmoji(msg.imgName, msg.namePLayerEmoji);
                    multiplayerScreenGuessCtrl.showEmoji(msg.imgName, msg.namePLayerEmoji);
                });
            default:
                // invalid msg type
        }
    }

    public void showQuestionS(ServerMessage msg){
        switch (msg.question.type){
            case COMPARE:
                singleplayerScreenCtrl.restoreView();
                singleplayerScreenCtrl.displayActivities(msg.question, singleplayerScreen);
                singleplayerScreenCtrl.setScoreTo(msg.score);
                singleplayerScreenCtrl.setTitleTo(msg.question.title);
                singleplayerScreenCtrl.setTimer(msg.timerFraction, msg.timerFull);
                showSingleplayerGameScreen();
                break;
            case GUESS:
            case HOW_MANY_TIMES:
                singleplayerScreenGuessCtrl.restoreView();
                singleplayerScreenGuessCtrl.displayActivities(msg.question, singleplayerGuessScreen);
                singleplayerScreenGuessCtrl.setTimer(msg.timerFraction, msg.timerFull);
                singleplayerScreenGuessCtrl.setHeadGuessTitle(msg.question.title);
                singleplayerScreenGuessCtrl.setScoreTo(msg.score);
                showSingleplayerGuessGameScreen();
                break;
            case ESTIMATION:
                //                singleplayerScreenInputCtrl.restoreView();
                singleplayerScreenInputCtrl.displayActivities(msg.question, singleplayerInputScreen);
                singleplayerScreenInputCtrl.setTimer(msg.timerFraction, msg.timerFull);
                singleplayerScreenInputCtrl.setTitleTo(msg.question.title);
                singleplayerScreenInputCtrl.setScoreTo(msg.score);
                showSingleplayerInputGameScreen();
                break;

            default:
                System.out.println("weird question");
        }
    }

    public void showQuestionM(ServerMessage msg){
        switch (msg.question.type){
            case HOW_MANY_TIMES:
            case GUESS:
                multiplayerScreenGuessCtrl.setTimer(msg.timerFraction, msg.timerFull);
                multiplayerScreenGuessCtrl.setHeadGuessTitle(msg.question.title);
                multiplayerScreenGuessCtrl.displayActivities(msg.question, guessQuestionM);
                showMultiplayerGuessScreen();
                break;
            case ESTIMATION:
                multiplayerScreenInputCtrl.setTimer(msg.timerFraction, msg.timerFull);
                multiplayerScreenInputCtrl.setHeadTitle(msg.question.title);
                multiplayerScreenInputCtrl.displayActivities(msg.question, inputQuestionM);
                showMultiplayerInputScreen();
                break;
            case COMPARE:
                multiplayerScreenCtrl.setTimer(msg.timerFraction, msg.timerFull);
                multiplayerScreenCtrl.setHeadTitle(msg.question.title);
                multiplayerScreenCtrl.displayActivities(msg.question, multiplayer);
                showMultiplayerScreen();
                break;
            default:
                System.out.println("weird question type");
                break;
        }
    }

    public void showInbetweenScore() {
        primaryStage.setTitle("Score");
        primaryStage.setScene(inBetweenScore);
    }

    public void showLeave(OnLeaveAction afterLeave){
        leaveCtrl.setAfterLeave(afterLeave);

        this.stage = new Stage();
        this.stage.setScene(leave);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    public void showLeave(OnLeaveAction afterLeave, OnLeaveAction beforeLeave){
        leaveCtrl.setBeforeLeave(beforeLeave);

        showLeave(afterLeave);
    }

    public void showInputServer(){
        inputServerScreenCtrl.render(server.isConnected());

        this.stage = new Stage();

        this.stage.setScene(inputServer);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();

        splashScreenCtrl.render();
    }

    public void closePopup() {
        this.stage.close();
    }

    public void stayWaitingroom(Scene previous){
        primaryStage.setScene(previous);
    }

    public void stay(Scene previous){
        primaryStage.setScene(previous);
    }

    public void showSplash(){
        splashScreenCtrl.render();
        primaryStage.setTitle("SplashScreen");
        primaryStage.setScene(splash);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public void showMultiplayerScreen(){
        primaryStage.setTitle("Multiplayer");
        primaryStage.setScene(multiplayer);
    }

    public void showMultiplayerInputScreen(){
        primaryStage.setTitle("Multiplayer");
        primaryStage.setScene(inputQuestionM);
    }

    public void showMultiplayerGuessScreen(){
        primaryStage.setTitle("Multiplayer");
        primaryStage.setScene(guessQuestionM);
    }

    public void showSingleLeaderboardScreen(){
        if(!server.isConnected()){
            showInputServer();
            return;
        }

        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(singleLeaderboard);

        singleplayerLeaderboardCtrl.insertLeaderboard();
    }

    public void showWaitingRoom() {
        primaryStage.setTitle("WaitingRoomScreen");
        primaryStage.setScene(waitingRoom);
        primaryStage.setOnCloseRequest(e -> {
            if(primaryStage.getScene().equals(waitingRoom)) {
                server.send("/app/general",
                        new ClientMessage(ClientMessage.Type.QUIT_WAITING_ROOM, getClientID(), getGameID()));
            }else{
                server.send("/app/general", new ClientMessage(ClientMessage.Type.QUIT, clientID, gameID));
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public void showinputNameScreen() {
        if(!server.isConnected()){
            showInputServer();
            return;
        }

        primaryStage.setTitle("input Name");
        primaryStage.setScene(inputName);
    }

    public void showSingleplayerGameScreen(){
        primaryStage.setTitle("Singleplayer");
        primaryStage.setScene(singleplayerScreen);
    }

    public void showSingleplayerGuessGameScreen(){
        primaryStage.setTitle("Singleplayer");
        primaryStage.setScene(singleplayerGuessScreen);
    }

    public void showSingleplayerInputGameScreen(){
        primaryStage.setTitle("Singleplayer");
        primaryStage.setScene(singleplayerInputScreen);
    }

    public void showAdminPanel() {
        if(!server.isConnected()){
            showInputServer();
            return;
        }

        adminPanelCtrl.initialize();
        adminPanelCtrl.displayActivities();
        primaryStage.setTitle("AdminPanel");
        primaryStage.setScene(adminPanel);
    }

    public void showHelp() {
        this.stage = new Stage();

        this.stage.setScene(help);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.showAndWait();
    }

    public void showEditActivity(Activity selected) {
        if(selected != null){
            primaryStage.setTitle("EditActivity");
            primaryStage.setScene(editActivity);
            editActivityCtrl.fillActivity(selected);
            editActivityCtrl.resetErrorText();
        }
    }

    public void showCreateActivity() {
        primaryStage.setTitle("CreateActivity");
        primaryStage.setScene(createActivity);
        createActivityCtrl.resetErrorText();
        createActivityCtrl.clearFields();
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

    public AdminPanelCtrl getAdminPanelCtrl() {
        return adminPanelCtrl;
    }

    public Scene getAdminPanel(){
        return adminPanel;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Sets the server name.
     * @param serverName server name
     */
    public void setServerName(String serverName){
        this.server.setServerURL(serverName);
    }

    public boolean connectToServer(String url){
        this.server.setServerURL(url);

        // try to get new clientID
        try{
            this.clientID = server.getClientID();
        }
        catch(Exception e){
            System.out.println("SERVER FAILED with exception " + e.getMessage());
            return false;
        }

        // try to connect with websockets
        if(!server.reconnect()) return false;

        // all works: register for websocket messages
        server.registerForMessage("/topic/client/" + clientID, ServerMessage.class, this::handleServerMessage);
        return true;
    }

    public ServerUtils getServer() {
        return server;
    }

    /**
     * check connection to server
     * @return boolean value true if the connection exists, false if it doesn't
     */
    public boolean checkServerConnection(){
        return server.isConnected();
    }

    public Scene getEditActivity() {
        return editActivity;
    }

    public Scene getCreateActivity() {
        return createActivity;
    }

    public Scene getInputQuestionM() {
        return inputQuestionM;
    }

    public Scene getGuessQuestionM() {
        return guessQuestionM;
    }

    public Scene getSingleplayerGuessScreen() {
        return singleplayerGuessScreen;
    }

    public Scene getSingleplayerInputScreen() {
        return singleplayerInputScreen;
    }
}