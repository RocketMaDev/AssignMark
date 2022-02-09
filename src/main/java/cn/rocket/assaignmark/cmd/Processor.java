package cn.rocket.assaignmark.cmd;

import cn.rocket.assaignmark.LocalURL;
import cn.rocket.assaignmark.core.AMFactory;
import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.exception.AssigningException;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Rocket
 * @version 1.0.8
 * @since 0.9.8
 */
public class Processor extends Main {
    public static final String[] msgList = {
            "正在加载赋分表...", "正在检查赋分表...", "正在加载分数表...", "正在检查分数表...",
            "正在赋分 政治 ...", "正在赋分 历史 ...", "正在赋分 地理 ...", "正在赋分 物理 ...", "正在赋分 化学 ...",
            "正在赋分 生物 ...", "正在赋分 技术 ...", "正在导出...", "完成！",
            "错误： 未找到赋分表", "错误： 无法读取赋分表", "错误： 赋分表不是标准xlsx表格", "错误： 赋分表格式不符合规范！请查阅说明",
            "错误： 未经验证的赋分表！请使用-e参数导出的赋分表",
            "错误： 未找到分数表", "错误： 无法读取分数表", "错误： 分数表不是标准xlsx表格", "错误： 分数表格式不符合规范！请查阅说明",
            "错误： 写出失败！非法的导出路径，或无权限写入。请确保导出表格没有在Excel等应用中打开",
            "错误： 分数表必须包含可赋分的工作表", "错误： 分数表路径与输出路径不能一致", "错误： 未在意料中的错误", "错误： 线程中断",
            "错误： 无法关闭工作表"
    };

    public static void main(String[] args) {
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
                LOGGER.info("\n A,I,O选项在赋分时必须使用，输出应与前两文件不同\n" +
                        " 不进行赋分，可选h,e参数\n" +
                        " 无参数则启动gui界面");
                return;
            } else if (cl.hasOption("e")) {
                try {
                    LOGGER.info("开始导出...");
                    AMFactory.tryToExtract(LocalURL.JAR_PARENT_PATH);
                    LOGGER.info("完成！");
                    return;
                } catch (IOException ioException) {
                    handleException(ioException, "复制失败!");
                } catch (AssigningException exception) {
                    handleException(exception, null);
                }
                return;
            } else if (!cl.hasOption('A') || !cl.hasOption('I') || !cl.hasOption('O')) {
                throw new ParseException("要赋分，必须包含A、I、O参数！");
            }

            assigningTablePath = cl.getOptionValue('A');
            markTablePath = cl.getOptionValue('I');
            outputPath = cl.getOptionValue('O');

            if (fileEqual(assigningTablePath, outputPath)) {
                LOGGER.warn("您正在尝试将输出文件覆盖到赋分表，确定吗？[y/N]");
                Scanner scanner = new Scanner(System.in);
                if (!scanner.nextLine().equalsIgnoreCase("y")) {
                    scanner.close();
                    return;
                }
                scanner.close();
            }
        } catch (ParseException e) {
            handleException(e, "参数有误！请使用-h参数查看详细使用指南");
        }
        AMEventHandler handler = (e, msg) -> {
            if (e.getIndex() >= AMEvent.ERR_AT_NOT_FOUND.getIndex()) {
                LOGGER.fatal(msgList[e.ordinal()]);
                if (msg != null)
                    LOGGER.error(msg);
            } else
                LOGGER.info(msgList[e.ordinal()]);
        };
        assert assigningTablePath != null && markTablePath != null && outputPath != null;
        new AMFactory(assigningTablePath, markTablePath, handler, outputPath).work();
    }

    public static boolean fileEqual(String path0, String path1) {
        File one = new File(path0);
        File other = new File(path1);
        try {
            return one.getCanonicalFile().equals(other.getCanonicalFile());
        } catch (IOException e) {
            return one.equals(other);
        }
    }
}
