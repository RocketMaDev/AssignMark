package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
import cn.rocket.assaignmark.cmd.Processor;
import cn.rocket.assaignmark.core.AMFactory;
import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.exception.AssigningException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Rocket
 * @version 1.0.8
 * @since 1.0.8
 */
public class Controller {
    private final FileChooser chooser;
    private boolean confirmation;

    private Thread task;

    {
        chooser = new FileChooser();
        chooser.setTitle("选择您的文件");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel文件", "*.xlsx"));
        chooser.setInitialDirectory(new File(LocalURL.JAR_PARENT_PATH));
        // TODO 设置初始路径，读取/保存到配置文件
    }

    public Label progressLabel;
    public Label statusLabel;
    public JFXProgressBar progressBar;
    public AnchorPane pane;
    public JFXTextField atField;
    public JFXTextField mtField;
    public JFXTextField outField;
    public JFXButton btn0;
    public AnchorPane ctrlPane;

    private Stage copyrightWindow;

    public void initialize() {
        Parent copyright;
        URL copyrightURL = LocalURL.class.getResource(LocalURL.COPYRIGHT_FXML_PATH);
        assert copyrightURL != null;
        try {
            copyright = FXMLLoader.load(copyrightURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        copyrightWindow = new Stage();
        copyrightWindow.setResizable(false);
        copyrightWindow.setScene(new Scene(copyright));
        copyrightWindow.setAlwaysOnTop(true);
        copyrightWindow.getIcons().add(new Image(LocalURL.ICON_PATH));
        copyrightWindow.setTitle("关于窗口");
        copyrightWindow.setResizable(false);
    }

    public void exportM() {
        Alert alert;
        try {
            AMFactory.tryToExtract(LocalURL.JAR_PARENT_PATH);
        } catch (IOException e) {
            alert = new Alert("复制失败！请尝试把程序放在可以写入文件的位置\n" + e.getLocalizedMessage(),
                    this, HintType.ERROR, false);
            alert.setEventHandler(null, null);
            alert.show();
            return;
        } catch (AssigningException e) {
            alert = new Alert("当前文件夹下赋分表太多了！请删除一些", this, HintType.ERROR, false);
            alert.setEventHandler(null, null);
            alert.show();
            return;
        }
        alert = new Alert("复制成功！", this, HintType.DONE, false);
        alert.setEventHandler(null, null);
        alert.show();
    }

    private void fillField(TextField field, boolean isReading) {
        File file;
        if (isReading)
            file = chooser.showOpenDialog(Launcher.mainStage);
        else
            file = chooser.showSaveDialog(Launcher.mainStage);
        if (file == null)
            return;
        field.setText(file.getAbsolutePath());
        field.appendText("");
        chooser.setInitialDirectory(file.getParentFile());
    }

    public void setAtM() {
        fillField(atField, true);
    }

    public void setMtM() {
        fillField(mtField, true);
    }

    public void setOutM() {
        fillField(outField, false);
    }

    public void startM() {
        if (atField.getText().isEmpty() || mtField.getText().isEmpty() || outField.getText().isEmpty()
                || task != null && task.isAlive())
            return;
        if (Processor.fileEqual(atField.getText(), outField.getText()) && !confirmation) {
            Alert alert = new Alert("您正在尝试将输出文件覆盖到赋分表，确定吗？",
                    this, HintType.HINT, true);
            alert.setEventHandler(event -> {
                confirmation = true;
                alert.close();
                startM();
            }, null);
            alert.show();
            return;
        }
        confirmation = false;
        File outFile = AMFactory.defaultGetFile(outField.getText());
        if (!outFile.exists())
            // noinspection ResultOfMethodCallIgnored
            outFile.getParentFile().mkdirs();
        ctrlPane.setDisable(true);
        task = new Thread(() -> {
            try {
                new AMFactory(
                        atField.getText(), mtField.getText(), new GUIEventHandler(this), outField.getText()
                ).work();
            } catch (RuntimeException e) {
                if (!(e.getCause() instanceof InterruptedException))
                    throw e;
            }
        }, "Assigning Task Thread");
        Launcher.mainStage.setOnCloseRequest(event -> {
            if (task.isAlive() && !task.isInterrupted())
                task.interrupt();
            event.consume();
            unlockWindow();
        });
        task.start();
    }

    private class GUIEventHandler implements AMEventHandler {
        private final int max = AMEvent.DONE.ordinal() + 1;
        private final Controller ctrler;

        public GUIEventHandler(Controller controller) {
            ctrler = controller;
        }

        {
            Platform.runLater(() -> {
                progressBar.setVisible(true);
                progressBar.setStyle(".jfx-progress-bar>.bar {\n" +
                        "    -fx-background-color: DODGERBLUE;\n" +
                        "}");
                progressBar.setProgress(-1);
                progressLabel.setText(String.format("%d/%d", 0, max));
                statusLabel.setText("正在初始化...");
            });
        }

        @Override
        public void handle(AMEvent event, String msg) {
            int index = event.getIndex();
            Platform.runLater(() -> {
                if (index < AMEvent.DONE.getIndex()) {
                    progressBar.setProgress((double) (index + 1) / max);
                    progressLabel.setText(String.format("%d/%d", index + 1, max));
                    statusLabel.setText(Processor.msgList[index]);
                } else if (index == AMEvent.DONE.getIndex()) {
                    progressBar.setStyle(".jfx-progress-bar>.bar {\n" +
                            "    -fx-background-color: GREEN;\n" +
                            "}");
                    progressBar.setProgress(1);
                    progressLabel.setText(String.format("%d/%d", max, max));
                    statusLabel.setText(Processor.msgList[index]);

                    Alert alert = new Alert(Processor.msgList[index], ctrler, HintType.DONE, false);
                    alert.setEventHandler(null, null);
                    alert.show();
                } else if (index <= AMEvent.getLastEvent().getIndex()) {
                    progressBar.setStyle(".jfx-progress-bar>.bar {\n" +
                            "    -fx-background-color: RED;\n" +
                            "}");
                    statusLabel.setText(statusLabel.getText() + "  失败！");
                    String message = Processor.msgList[event.ordinal()] + (msg == null ? "" : "\n" + msg);
                    Alert alert = new Alert(message, ctrler, HintType.ERROR, false);
                    alert.setEventHandler(null, null);
                    alert.show();
                }
            });
        }
    }

    public void copyrightM() {
        if (!copyrightWindow.isShowing())
            copyrightWindow.show();
    }

    void lockWindow() {
        Launcher.mainStage.setOnCloseRequest(Event::consume);
        ctrlPane.setDisable(true);
    }

    void unlockWindow() {
        Launcher.mainStage.setOnCloseRequest(event -> {
        });
        ctrlPane.setDisable(false);
    }
}
