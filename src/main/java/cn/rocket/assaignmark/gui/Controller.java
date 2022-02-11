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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 核心窗口的Controller
 *
 * @author Rocket
 * @version 1.1.8
 * @since 1.0.8
 */
public class Controller {
    private final Properties properties = new Properties();
    private final FileChooser chooser;
    private boolean confirmation;
    private String openedPath;

    private Thread task;

    {
        File propertiesFile = new File(LocalURL.PROPERTIES_PATH);
        boolean canMake = true;
        boolean exist = propertiesFile.exists();
        if (!exist)
            canMake = propertiesFile.getParentFile().mkdirs();
        String toBeLoad = LocalURL.JAR_PARENT_PATH;
        if (canMake) {
            if (exist)
                try (FileInputStream in = new FileInputStream(propertiesFile)) {
                    properties.load(in);
                    toBeLoad = properties.getProperty("initialPath");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (openedPath != null) {
                    try (FileOutputStream out = new FileOutputStream(propertiesFile)) {
                        properties.setProperty("initialPath", openedPath);
                        properties.store(out, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }

        chooser = new FileChooser();
        chooser.setTitle("选择您的文件");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel文件", "*.xlsx"));
        chooser.setInitialDirectory(new File(toBeLoad));
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
        openedPath = file.getParentFile().getAbsolutePath();
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
        // 检查三个文本框是否为空
        if (atField.getText().isEmpty() || mtField.getText().isEmpty() || outField.getText().isEmpty()
                || task != null && task.isAlive())
            return;

        // 检查输出文件是否与赋分表一致
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

        // 创建文件路径，准备开始
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
            } catch (Exception ignored) {
            }
        }, "Assigning Task Thread");
        // 在运行任务时按关闭会先终止任务线程
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
                    statusLabel.setText(Processor.MSG_ARR[index]);

                } else if (index == AMEvent.DONE.getIndex()) {
                    progressBar.setProgress(1);
                    progressLabel.setText(String.format("%d/%d", max, max));
                    statusLabel.setText(Processor.MSG_ARR[index]);

                    Alert alert = new Alert(Processor.MSG_ARR[index], ctrler, HintType.DONE, false);
                    alert.setEventHandler(null, null);
                    alert.show();

                } else if (index <= AMEvent.getLastEvent().getIndex()) {
                    String message = Processor.MSG_ARR[event.ordinal()]; // 事件信息
                    boolean unexpected = false;
                    if (msg != null) {
                        message += "\n";
                        boolean fail;
                        if ((fail = msg.startsWith(AMEvent.ERR_FAILED_TO_CLOSE.toString())) || event == AMEvent.ERR_UNEXPECTED) {
                            unexpected = true;
                            message += msg.substring(0, msg.indexOf('\n', fail ? // 截断事件栈帧
                                    AMEvent.ERR_FAILED_TO_CLOSE.toString().length() + 2 : 0)); // if fail -> ERR_FAILED_TO_CLOSE\n_
                            String name = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()); // 生成文件名
                            Path path = null;
                            try {
                                path = new File(LocalURL.JAR_PARENT_PATH + name + ".txt").getCanonicalFile().toPath();
                            } catch (IOException e) {
                                LogManager.getRootLogger().error("Failed to get file name to save: " +
                                        e.getLocalizedMessage());
                            }
                            if (path != null) {
                                try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                                    writer.write(msg);
                                } catch (IOException e) {
                                    LogManager.getRootLogger().error("Can't write error log out: " +
                                            e.getLocalizedMessage());
                                }
                                message += "\n发生意料之外的异常，已保存到jar路径下的文件中，" +
                                        "按确定以复制错误信息（建议复制到word中防止丢失），并请到GitHub/Gitee上发issue";
                            } else
                                message += "\n发生意料之外的异常，" +
                                        "按确定以复制错误信息（建议复制到word中防止丢失），并请到GitHub/Gitee上发issue";
                        } else
                            message += msg;
                    }
                    statusLabel.setText(statusLabel.getText() + "  失败！");
                    Alert alert = new Alert(message, ctrler, HintType.ERROR, true);
                    EventHandler<ActionEvent> handler = null;
                    // 复制
                    if (unexpected)
                        handler = event1 -> {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null);
                            alert.close();
                        };

                    alert.setEventHandler(handler, null); // handler默认为null!
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
