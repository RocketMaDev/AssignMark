package cn.rocket.assaignmark.cmd;

import org.apache.commons.cli.*;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class Main {
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "help", false, "打印此帮助信息");
        options.addOption("e", "export", false, "导出默认赋分表到当前路径");
        options.addOption("A", "table", true, "输入赋分表的路径");
        options.addOption("I", "input", true, "输入分数表的路径");
        options.addOption("O", "output", true, "输出分数表的路径");


        try {
            HelpFormatter formatter = new HelpFormatter();
            CommandLine cl = parser.parse(options, args);
            if (cl.hasOption("h")) {
                formatter.printHelp("AssignMark.jar", options);
                System.out.println("\n A,I选项在赋分时必须使用，O选项可选，表示覆盖原文件\n" +
                        " 不进行赋分，可选h,e参数\n" +
                        " 无参数则启动gui界面");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
