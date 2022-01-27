package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
import com.jfoenix.controls.JFXButton;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Rocket
 * @version 0.9.8
 */
public class Alert {
    private final Stage alertStage = new Stage(StageStyle.UNDECORATED);
    private static final double GAP = 25;
    private static final double SMALL_GAP = 10;
    private static final double FONT_SIZE = 22;
    private static final double ICON_WIDTH = 75;

    private final ImageView view;
    private final Controller controller;
    private final JFXButton ok;
    private final JFXButton cancel;
    private final AnchorPane anchorPane;
    private final Label text;

    public Alert(String message, Controller controller, HintType type, boolean enableCancel) {
        assert controller != null;
        this.controller = controller;
        controller.lockWindow();

        ok = new JFXButton("确定");
        ok.setTextFill(Paint.valueOf("DODGERBLUE"));
        AnchorPane.setBottomAnchor(ok, GAP);
        AnchorPane.setRightAnchor(ok, GAP);

        text = new Label(message);
        text.setWrapText(true);
        AnchorPane.setTopAnchor(text, GAP);
        AnchorPane.setLeftAnchor(text, GAP);
        AnchorPane.setRightAnchor(text, GAP);

        if (type != null) {
            view = new ImageView(type.getURL());
            AnchorPane.setTopAnchor(view, GAP);
            AnchorPane.setLeftAnchor(view, GAP);
            AnchorPane.setLeftAnchor(text, GAP + ICON_WIDTH + SMALL_GAP);
        } else
            view = null;

        if (enableCancel) {
            cancel = new JFXButton("取消");
            cancel.setTextFill(Paint.valueOf("DODGERBLUE"));
            AnchorPane.setBottomAnchor(cancel, GAP);
        } else
            cancel = null;

        anchorPane = new AnchorPane();
        anchorPane.getStylesheets().add(LocalURL.ALERT_CSS_PATH);
        anchorPane.setStyle(".label {\n    -fx-font-size: " + FONT_SIZE + ";\n}");
        ObservableList<Node> list = anchorPane.getChildren();
        list.addAll(ok, text);
        if (enableCancel)
            list.add(cancel);
        if (type != null)
            list.add(view);
        anchorPane.setPrefWidth(700);
        anchorPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("WHITE"), null, null)));

        Scene scene = new Scene(anchorPane);
        alertStage.setScene(scene);
        alertStage.setResizable(false);
        alertStage.setAlwaysOnTop(true);
        alertStage.setOnCloseRequest(event -> close());
    }

    public void setEventHandler(EventHandler<ActionEvent> okHandler, EventHandler<ActionEvent> cancelHandler) {
        ok.setOnAction(okHandler != null ? okHandler : event -> close());
        if (cancel != null)
            cancel.setOnAction(cancelHandler != null ? cancelHandler : event -> close());
    }

    public void show() {
        alertStage.show();
        if (cancel != null)
            AnchorPane.setRightAnchor(cancel, GAP + ok.getWidth() + GAP);
        if (view == null || view.getFitHeight() < text.getHeight())
            anchorPane.setPrefHeight(GAP + text.getHeight() + 50 + ok.getHeight() + GAP);
        else
            anchorPane.setPrefHeight(GAP + view.getFitHeight() + 50 + ok.getHeight() + GAP);
        alertStage.sizeToScene();
    }

    public void close() {
        alertStage.close();
        controller.unlockWindow();
    }
}
