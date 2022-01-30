package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
import cn.rocket.assaignmark.cmd.Processor;
import cn.rocket.assaignmark.core.AMFactory;
import cn.rocket.assaignmark.core.exception.AssigningException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
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
        if (atField.getText().isEmpty() || mtField.getText().isEmpty() || outField.getText().isEmpty())
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
        Alert alert = new Alert("尚未完成...请等待1.1.8版本", this, null, false);
        alert.setEventHandler(null, null);
        alert.show();
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
