package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Rocket
 * @version 0.9.8
 */
public class Alert {
    private final Stage alertStage = new Stage(StageStyle.UNDECORATED);
    private static final double GAP = 25;
    private static final double FONT_SIZE = 22;
    private final Controller controller;
    private final JFXButton ok;
    private final JFXButton cancel;
    private final AnchorPane anchorPane;
    private final Label text;

    public Alert(String message, Controller controller) {
        assert controller != null;
        this.controller = controller;

        ok = new JFXButton("确定");
        ok.setTextFill(Paint.valueOf("DODGERBLUE"));
        AnchorPane.setBottomAnchor(ok, GAP);
        AnchorPane.setRightAnchor(ok, GAP);

        cancel = new JFXButton("取消");
        cancel.setTextFill(Paint.valueOf("DODGERBLUE"));
        AnchorPane.setBottomAnchor(cancel, GAP);

        text = new Label(message);
        text.setFont(Font.font(FONT_SIZE));
        text.setWrapText(true);
        AnchorPane.setTopAnchor(text, GAP);
        AnchorPane.setLeftAnchor(text, GAP);
        AnchorPane.setRightAnchor(text, GAP);

        anchorPane = new AnchorPane();
        anchorPane.getStylesheets().add(LocalURL.ROOT_CSS_PATH);
        anchorPane.setStyle(".label {\n    -fx-font-size: " + FONT_SIZE + ";\n}");
        anchorPane.getChildren().addAll(ok, cancel, text);
        anchorPane.setPrefWidth(700);
        anchorPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("WHITE"), null, null)));

        Scene scene = new Scene(anchorPane);
        alertStage.setScene(scene);
        alertStage.setAlwaysOnTop(true);
//        alertStage.setOnCloseRequest(event -> GlobalVariables.mwObj.unlockMainWindow());
    }

    public void setEventHandler(EventHandler<ActionEvent> okHandler, EventHandler<ActionEvent> cancelHandler) {
        ok.setOnAction(okHandler);
        cancel.setOnAction(cancelHandler);
    }

    public void show() {
        alertStage.show();
        AnchorPane.setRightAnchor(cancel, GAP + ok.getWidth() + GAP);
        anchorPane.setPrefHeight(GAP + text.getHeight() + 50 + ok.getHeight() + GAP);
        alertStage.sizeToScene();
    }

    public void close() {
        alertStage.close();
    }
}
