package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.LocalURL;
import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import cn.rocket.assaignmark.core.exception.AssigningException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 赋分工厂类
 * <p>
 * 用以提供便捷的生成打包的赋分对象的服务
 *
 * @author Rocket
 * @version 1.1.8
 * @since 0.9.8
 */
public class AMFactory {
    private final String assigningTablePath;
    private final String markTablePath;
    private final AMEventHandler handler;
    private final String outputPath;
    private final Notifier notifier;

    /**
     * 构造一个工厂类的实例，使用同一个<code>AMEvent</code>事件处理器，串行完成任务，统一进行调度
     * <p>
     * 所有参数基本不可为<code>null</code>
     *
     * @param assigningTablePath 赋分表路径
     * @param markTablePath      分数表路径
     * @param handler            <code>AMEvent</code>事件处理器，<i>此项可为<code>null</code></i>
     * @param outputPath         输出赋分完毕的分数表的路径
     */
    public AMFactory(String assigningTablePath, String markTablePath, AMEventHandler handler, String outputPath) {
        this.assigningTablePath = assigningTablePath;
        this.markTablePath = markTablePath;
        this.handler = handler;
        this.outputPath = outputPath;
        this.notifier = new Notifier(handler);
    }

    /**
     * 提取赋分表模板，使用<code>Files.copy()</code>
     *
     * @param outputPath 赋分表模板输出路径
     * @throws IOException 如果无法写出
     * @see Files#copy(Path, OutputStream)
     */
    public static void extractTable(String outputPath) throws IOException {
        try (InputStream is = AMFactory.class.getResourceAsStream(LocalURL.TEMPLATE_PATH)) {
            assert is != null;
            Files.copy(is, Paths.get(outputPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 尝试提取赋分表模板，会在<code>parentPath</code>中尝试11次，以 <code>"赋分表"+数字+".xlsx"</code> 命名
     * @param parentPath 尝试新建文件的路径
     * @throws IOException 如果无法创建文件
     * @throws AssigningException 如果过多赋分表已存在在当前路径
     */
    public static void tryToExtract(String parentPath) throws IOException, AssigningException {
        if (parentPath.charAt(parentPath.length() - 1) != File.separatorChar)
            parentPath += "/";
        File file = new File(parentPath + "赋分表.xlsx");
        if (file.exists())
            for (int i = 0; i < 10; i++) {
                file = new File(LocalURL.JAR_PARENT_PATH + "赋分表" + i + ".xlsx");
                if (!file.exists())
                    break;
            }
        if (file.exists())
            throw new AssigningException("过多赋分表已存在在当前路径！");
        extractTable(file.getPath());
    }

    /**
     * 安全的。
     * <p>
     * 开始赋分，全流程包括：检查并读取赋分表比例、人数，检查并读取分数表分数，分科目分别赋分，最后输出
     * <p>
     * 在线程中断(interrupt)时，会扔出<code>RuntimeException</code>包装的{@link InterruptedException}
     */
    public void work() {
        try {
            impl_work();
        } catch (AssigningException ignored) {
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } catch (Exception e) {
            notifier.notify(AMEvent.ERR_UNEXPECTED, getExceptionStack(e));
            throw e;
        }
    }

    /**
     * <b>不安全。除非你想自己处理异常。</b>
     * <p>
     * 开始赋分。
     *
     * @throws AssigningException 发生已定义的异常
     * @see AMFactory#work()
     */
    public void impl_work() throws AssigningException, InterruptedException {
        AssigningTable at = new AssigningTable(assigningTablePath, handler, notifier, null);
        at.checkAndLoad();
        MarkTable mt = new MarkTable(markTablePath, handler, outputPath, at, notifier, null);
        mt.checkAndLoad();
        mt.calcAssignedMarks();
    }

    /**
     * 返回<code>child</code>指向的文件，自动判断是相对的还是绝对的
     *
     * @param parent 如果<code>child</code>是相对路径，那么以其为父路径
     * @param child  如果为绝对路径，则不变，否则用<code>parent</code>补全路径
     * @return 对应的 {@code File} 对象，使用处理过的路径（<code>canonical</code>）
     */
    public static File getFile(String parent, String child) {
        File file = new File(child);
        File outFile;
        if (file.isAbsolute())
            outFile = file;
        else
            outFile = new File(parent, child);
        try {
            return outFile.getCanonicalFile();
        } catch (IOException e) {
            return outFile;
        }
    }

    /**
     * 返回<code>path</code>指向的文件
     *
     * @param path 如果为绝对路径，则不变，否则用jar所在路径补全路径
     * @return 对应的 {@code File} 对象，使用处理过的路径（<code>canonical</code>）
     * @see AMFactory#getFile(String, String)
     */
    public static File defaultGetFile(String path) {
        return getFile(LocalURL.JAR_PARENT_PATH, path);
    }

    /**
     * 将异常打印到字符串中
     *
     * @param e 要保存的异常
     * @return 包含异常栈帧的字符串
     */
    public static String getExceptionStack(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /**
     * 将异常打印到字符串中，并附加<i>未关闭的文件</i>事件到开头
     *
     * @param e 要包含的异常
     * @return 包含事件与异常栈帧信息的字符串
     * @see AMEvent#ERR_FAILED_TO_CLOSE
     * @see AMFactory#getExceptionStack(Exception)
     */
    public static String attachUnclosedEvent(IOException e) {
        return AMEvent.ERR_FAILED_TO_CLOSE + ":\n" + getExceptionStack(e);
    }
}