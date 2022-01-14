package cn.rocket.assaignmark;

import cn.rocket.assaignmark.cmd.Main;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 程序中存放资源地址的工具类
 *
 * @author Rocket
 * @version 0.9.8
 */
public final class LocalURL {
    private LocalURL() {
    }

    // Jar associated
    public static final String JAR_PATH; // with /
    public static final String JAR_PARENT_PATH; // with /
    // Resources associated
    public static final String RES_PATH = "/amres/";
    public static final String TEMPLATE_PATH = RES_PATH + "/core/template.xlsx";

    static {
        String jarPath;
        try {
            jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            jarPath = null;
            System.err.println("无法解析jar路径！");
        }
        JAR_PATH = jarPath;
        JAR_PARENT_PATH = new File(JAR_PATH).getParent() + "/";
    }
}
