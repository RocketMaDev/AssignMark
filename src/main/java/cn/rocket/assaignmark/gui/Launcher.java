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
 * @version 1.0.8
 * @since 1.0.8
 */
public class Launcher extends Application {
    static Stage mainStage;

    /**
     * 调用以启动图形化程序启动，避免在高版本ClassNotFound
     */
    public static void launchSelf() {
        Application.launch(Launcher.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 图形化程序“入口”
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(LocalURL.MAIN_FXML_PATH));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            LogManager.getRootLogger().error("Failed to load fxml:",e);
            System.exit(1);
        }
        primaryStage.setTitle("赋分程序");
        primaryStage.getIcons().add(new Image(LocalURL.ICON_PATH));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        mainStage = primaryStage;
    }
}
