package cn.rocket.assaignmark.cmd;

import cn.rocket.assaignmark.gui.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 主类
 *
 * @author Rocket
 * @version 1.1.8.1
 * @since 0.9.8
 */
public class Main {

    protected static final Logger LOGGER = LogManager.getLogger(Main.class);

    /**
     * 程序主入口
     *
     * @param args 外部传入参数
     */
    public static void main(String[] args) {
        String jreVer = System.getProperty("java.runtime.version");
        try {
            jreVer = jreVer.substring(0, jreVer.indexOf('.', 2));
        } catch (StringIndexOutOfBoundsException e) {
            LOGGER.warn("Unknown JRE version! Try to run with JRE 1.11. Unexpected errors may be emitted.");
            jreVer = "1.11";
        }
        if (args == null || args.length == 0) {
            if (jreVer.equals("1.8")) {
                LOGGER.info("Running in JRE 1.8. Suitable.");
            } else {
                LOGGER.fatal("Running in JRE " + jreVer + "! Please run this program in 1.8.");
                LOGGER.fatal("要使用图形化界面，只能使用java8！请使用java8启动此程序，或下载用于java17的版本（如果有）");
                System.exit(1);
            }
            Launcher.launchSelf();
        } else {
            if (jreVer.equals("1.8"))
                LOGGER.info("Running in JRE 1.8. Suitable");
            else
                LOGGER.warn("Running in JRE " + jreVer + ". I do not guarantee no unexpected errors. The program was " +
                        "written in 1.8.");
            Processor.main(args);
        }
    }

    /**
     * 处理主类异常
     *
     * @param e    异常实例
     * @param hint 提示信息
     */
    protected static void handleException(Exception e, String hint) {
        LOGGER.fatal("错误：" + e.toString());
        if (hint != null)
            LOGGER.error(hint);
        System.exit(0);
    }
}
