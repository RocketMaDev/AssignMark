package cn.rocket.assaignmark.gui;

import cn.rocket.assaignmark.LocalURL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

/**
 * @author Rocket
 * @version 0.9.8
 */
public class Launcher extends Application {
    static Stage mainStage;

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(LocalURL.MAIN_FXML_PATH));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            LogManager.getRootLogger().error("错误:" + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(1);
        }
        primaryStage.setTitle("赋分程序");
        primaryStage.getIcons().add(new Image(LocalURL.ICON_PATH));
        primaryStage.setScene(scene);
        primaryStage.show();
        mainStage = primaryStage;
    }
}
