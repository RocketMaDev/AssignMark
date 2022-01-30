package cn.rocket.assaignmark;

import cn.rocket.assaignmark.cmd.Main;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 程序中存放资源地址的工具类
 *
 * @author Rocket
 * @version 1.0.8
 * @since 0.9.8
 */
public final class LocalURL {
    private LocalURL() {
    }

    // Jar associated
    public static final String JAR_PATH; // with /
    public static final String JAR_PARENT_PATH; // with /

    // Resources associated
    private static final String RES_PATH = "/amres/";
    private static final String CORE_PATH = RES_PATH + "core/";
    public static final String TEMPLATE_PATH = CORE_PATH + "template.xlsx";

    private static final String GUI_PATH = RES_PATH + "gui/";
    public static final String MAIN_FXML_PATH = GUI_PATH + "Main.fxml";
    public static final String COPYRIGHT_FXML_PATH = GUI_PATH + "Copyright.fxml";
    public static final String ROOT_CSS_PATH = GUI_PATH + "root.css";
    public static final String ICON_PATH = GUI_PATH + "icon.png";
    public static final String ALERT_CSS_PATH = GUI_PATH + "alert.css";
    public static final String ICON_ERROR_PATH = GUI_PATH + "error.png";
    public static final String ICON_DONE_PATH = GUI_PATH + "done.png";
    public static final String ICON_HINT_PATH = GUI_PATH + "hint.png";

    // Website associated
    public static final String REPO_URL = "https://github.com/RocketMaDev/AssignMark";
    public static final String LICENSE_URL = REPO_URL + "/tree/maven/LICENSE%20OF%20USED%20LIBS";
    public static final String PERSONAL_URL = "https://github.com/RocketMaDev";

    static {
        String jarPath;
        try {
            jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            System.err.println("无法解析jar路径！");
            throw new RuntimeException(e);
        }
        JAR_PATH = jarPath;
        JAR_PARENT_PATH = new File(JAR_PATH).getParent() + "/";
    }
}
