package cn.rocket.assaignmark.cmd;

import cn.rocket.assaignmark.core.AMFactory;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.gui.Launcher;
import javafx.application.Application;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class Main {
    public static final String JAR_PATH; // with /
    public static final String JAR_PARENT_PATH; // with /
    private static final String[] msgList = {"正在加载赋分表...", "正在检查赋分表...", "正在加载分数表", "正在检查分数表",
            "正在赋分 政治", "正在赋分 历史", "正在赋分 地理", "正在赋分 物理", "正在赋分 化学", "正在赋分 生物", "正在赋分 技术",
            "正在导出", "完成！",
            "错误： 未找到赋分表", "错误： 无法读取赋分表", "错误： 赋分表不是标准xlsx表格", "错误： 赋分表格式不符合规范！请查阅说明",
            "错误： 未经验证的赋分表！请使用-e参数导出的赋分表",
            "错误： 未找到分数表", "错误： 无法读取分数表", "错误： 分数表不是标准xlsx表格", "错误： 分数表格式不符合规范！请查阅说明",
            "错误： 非法的导出路径", "错误： 写出失败！请确保导出表格没有在Excel等应用中打开", "错误： 分数表必须包含可赋分的工作表",
            "错误： 未在意料中的错误"};

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

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            Application.launch(Launcher.class);
            return;
        }

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "help", false, "打印此帮助信息");
        options.addOption("e", "export", false, "导出默认赋分表到当前路径");
        options.addOption("A", "table", true, "输入赋分表的路径");
        options.addOption("I", "input", true, "输入分数表的路径");
        options.addOption("O", "output", true, "输出分数表的路径");

        String assigningTablePath = null, markTablePath = null, outputPath = null;
        try {
            for (String arg : args)
                if (arg.contains("\""))
                    throw new ParseException("不支持解析\"!请将其替换成'");
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cl = parser.parse(options, args);
            if (cl.hasOption("h")) {
                formatter.printHelp("AssignMark.jar", options);
                System.out.println("\n A,I选项在赋分时必须使用，O选项可选，表示覆盖原文件\n" +
                        " 不进行赋分，可选h,e参数\n" +
                        " 无参数则启动gui界面");
                return;
            } else if (cl.hasOption("e")) {
                try {
                    File file = new File(JAR_PARENT_PATH + "赋分表.xlsx");
                    if (file.exists())
                        for (int i = 0; i < 10; i++) {
                            file = new File(JAR_PARENT_PATH + "赋分表" + i + ".xlsx");
                            if (!file.exists())
                                break;
                        }
                    if (file.exists())
                        throw new Exception("过多赋分表已存在在当前路径！");
                    System.out.println("正在复制赋分表...");
                    AMFactory.extractTable(file.getPath());
                    System.out.println("完成！");
                    return;
                } catch (IOException ioException) {
                    handleException(ioException, "复制失败!");
                } catch (Exception exception) {
                    handleException(exception, null);
                }
                return;
            } else if (!cl.hasOption('A') || !cl.hasOption('I')) {
                throw new ParseException("要赋分，必须包含A、I参数！");
            }

            assigningTablePath = cl.getOptionValue('A');
            markTablePath = cl.getOptionValue('I');
            if (!new File(assigningTablePath).exists() || !new File(markTablePath).exists())
                throw new FileNotFoundException("赋分表或分数表不存在");

            if (!cl.hasOption('O')) { // 询问是否覆盖
                Scanner scanner = new Scanner(System.in);
                System.out.println("尚未输入输出路径!是否覆盖原文件?[y/N]");
                if (!scanner.nextLine().trim().equals("y")) {
                    return;
                }
            }

            outputPath = cl.hasOption('O') ? cl.getOptionValue('O') : markTablePath;
        } catch (ParseException e) {
            handleException(e, "参数有误！请使用-h参数查看详细使用指南");
        } catch (FileNotFoundException e) {
            handleException(e, null);
        }
        AMEventHandler handler = (e, msg) -> {
            int index = e.getIndex();
            System.out.println(index <= 12 ? msgList[index] : msgList[index - 19]); // 关于19，详见AMEvent
            if (msg != null)
                System.out.println("错误提示：" + msg);
        };
        assert assigningTablePath != null && markTablePath != null && outputPath != null;
        new AMFactory(assigningTablePath, markTablePath, handler, outputPath).work();
    }

    private static void handleException(Exception e, String hint) {
        System.err.println("Error: " + e.toString());
        if (hint != null)
            System.err.println(hint);
        System.exit(0);
    }
}
