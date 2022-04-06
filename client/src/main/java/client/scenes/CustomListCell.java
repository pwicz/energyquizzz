package client.scenes;

import commons.Score;
import javafx.geometry.VPos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class CustomListCell extends ListCell<Score> {

    @Override
    protected void updateItem(Score item, boolean empty) {
        super.updateItem(item, empty);

        Pane pane = new Pane();
        if(!empty) {
            final Text leftText = new Text(item.getPlayerName());
            leftText.setTextOrigin(VPos.TOP);
            leftText.relocate(0,0);
            leftText.setStyle("-fx-fill: linear-gradient(to left, #1BED62, #21A0E8)");

            final Text rightText = new Text(String.valueOf(item.getPlayerScore()) + " Points!");
            rightText.setTextOrigin(VPos.TOP);
            final double width = rightText.getLayoutBounds().getHeight();
            rightText.relocate( 27*width- rightText.getLayoutBounds().getWidth(), 0);
            rightText.setStyle("-fx-fill: linear-gradient(to left, #1BED62, #21A0E8)");
            pane.getChildren().addAll(leftText, rightText);
        }
        setText("");
        setGraphic(pane);
    }
}
