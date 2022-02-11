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
 * 生成简易对话框，包含提示图案、信息以及确定、取消按钮
 *
 * @author Rocket
 * @version 1.1.8
 * @since 1.0.8
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

    /**
     * 生成一个对话框
     *
     * @param message      传入的要显示的信息
     * @param controller   控制窗口的Controller实例
     * @param type         要显示的提示类型，<code>null</code>则无图像
     * @param enableCancel 决定是否显示取消按钮
     */
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

    /**
     * 设置按钮点击事件处理器
     *
     * @param okHandler     确定键对应的事件处理器，<code>null</code>则关闭窗口
     * @param cancelHandler 取消键对应的事件处理器，<code>null</code>同上
     */
    public void setEventHandler(EventHandler<ActionEvent> okHandler, EventHandler<ActionEvent> cancelHandler) {
        ok.setOnAction(okHandler != null ? okHandler : event -> close());
        if (cancel != null)
            cancel.setOnAction(cancelHandler != null ? cancelHandler : event -> close());
    }

    /**
     * 显示对话框
     */
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

    /**
     * 关闭对话框
     */
    public void close() {
        alertStage.close();
        controller.unlockWindow();
    }
}
