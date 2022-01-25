package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * @author Rocket
 * @version 0.9.8
 */
public class Controller {
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
    }

    public void exportM() {
        Alert alert;
        try {
            AMFactory.tryToExtract(LocalURL.JAR_PARENT_PATH);
        } catch (IOException e) {
            alert = new Alert("复制失败！请尝试把程序放在可以写入文件的位置\n" + e.getLocalizedMessage(), this);
            alert.setEventHandler(null, null);
            alert.show();
            return;
        } catch (AssigningException e) {
            alert = new Alert("当前文件夹下赋分表太多了！请删除一些", this);
            alert.setEventHandler(null, null);
            alert.show();
            return;
        }
        alert = new Alert("复制成功！", this);
        alert.setEventHandler(null, null);
        alert.show();
    }

    public void setAtM() {
    }

    public void setMtM() {
    }

    public void setOutM() {
    }

    public void startM() {
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
