package cn.rocket.assaignmark;

import cn.rocket.assaignmark.core.AMFactory;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 程序中存放资源地址的工具类
 *
 * @author Rocket
 * @version 1.1.8
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

    static {
        String jarPath;
        try {
            jarPath = AMFactory.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            System.err.println("无法解析jar路径！");
            throw new RuntimeException(e);
        }
        JAR_PATH = jarPath;
        JAR_PARENT_PATH = new File(JAR_PATH).getParent() + "/";
    }
}
