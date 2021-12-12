package cn.rocket.assaignmark.cmd;

import cn.rocket.assaignmark.core.AMFactory;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.gui.Launcher;
import javafx.application.Application;
import org.apache.commons.cli.*;

import java.util.Scanner;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class Main {
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
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cl = parser.parse(options, args);
            if (cl.hasOption("h")) {
                formatter.printHelp("AssignMark.jar", options);
                System.out.println("\n A,I选项在赋分时必须使用，O选项可选，表示覆盖原文件\n" +
                        " 不进行赋分，可选h,e参数\n" +
                        " 无参数则启动gui界面");
                return;
            } else if (cl.hasOption("e")) {
                //TODO 模板
                System.out.println("正在复制...");
                return;
            } else if (!cl.hasOption('A') || !cl.hasOption('I'))
                throw new ParseException("");
            assigningTablePath = cl.getOptionValue('A');
            markTablePath = cl.getOptionValue('I');
            outputPath = cl.hasOption('O') ? cl.getOptionValue('O') : markTablePath;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (outputPath == markTablePath) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("尚未输入输出路径!是否覆盖原文件?[y/N]");
            if (!scanner.nextLine().equals("y"))
                return;
        }
        AMEventHandler handler = (e, msg) -> {
            System.out.println(msg);
        };
        assert assigningTablePath != null && markTablePath != null && outputPath != null;
        new AMFactory(assigningTablePath, markTablePath, handler, outputPath).work();
    }
}
