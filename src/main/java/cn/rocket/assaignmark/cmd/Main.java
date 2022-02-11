package cn.rocket.assaignmark.cmd;

import cn.rocket.assaignmark.gui.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 主类
 *
 * @author Rocket
 * @version 1.0.8
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
        if (args == null || args.length == 0) {
            if (!System.getProperty("java.runtime.version").startsWith("1.8.")) {
                LOGGER.fatal("要使用图形化界面，只能使用java8！请使用java8启动此程序，或下载用于java17的版本（如果有）");
                System.exit(1);
            }
            Launcher.launchSelf();
            return;
        }
        Processor.main(args);
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
