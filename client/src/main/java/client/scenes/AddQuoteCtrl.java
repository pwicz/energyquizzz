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

import com.google.inject.Inject;

import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class AddQuoteCtrl {

    private final MainCtrl mainCtrl;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField quote;

    @Inject
    public AddQuoteCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void cancel() {
        clearFields();
        mainCtrl.showOverview();
    }

    public void ok() {
//        try {
//            server.addQuote(getQuote());
//        } catch (WebApplicationException e) {
//
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.initModality(Modality.APPLICATION_MODAL);
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//            return;
//        }
        // the above commented out to use websockets

        try{
            // send the quote to /app/quotes so that the server can handle it
            mainCtrl.getServer().send("/app/quotes", getQuote());
        }
        catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverview();
    }

    private Quote getQuote() {
        var p = new Person(firstName.getText(), lastName.getText());
        var q = quote.getText();
        return new Quote(p, q);
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        quote.clear();
    }

}