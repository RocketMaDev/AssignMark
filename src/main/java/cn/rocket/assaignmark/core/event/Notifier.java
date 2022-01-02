package cn.rocket.assaignmark.core.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 事件唤醒器
 * <p>
 * 使用线程池以串行完成任务
 *
 * @author Rocket
 * @version 0.9.8
 */
public class Notifier {
    private final AMEventHandler handler;
    private final ExecutorService executor;
    private final boolean isNull;
    private final boolean printHint;

    /**
     * 构造事件唤醒器类，当<code>handler</code>不为<code>null</code>时新建线程池以串行运行任务
     *
     * @param handler 指定<code>AMEvent</code>处理器实例，<i>此项可为<code>null</code></i>
     * @see Executors#newSingleThreadExecutor()
     * @see AMEvent
     */
    public Notifier(AMEventHandler handler) {
        this(handler, true);
    }

    /**
     * 构造事件唤醒器类，当<code>handler</code>不为<code>null</code>时新建线程池以串行运行任务
     *
     * @param handler   指定<code>AMEvent</code>处理器实例，<i>此项可为<code>null</code></i>
     * @param printHint 指定是否需要打印提示信息到标准输出流
     * @see Executors#newSingleThreadExecutor()
     * @see AMEvent
     */
    public Notifier(AMEventHandler handler, boolean printHint) {
        this.printHint = printHint;
        executor = handler == null ? null : Executors.newSingleThreadExecutor();
        isNull = handler == null;
        this.handler = handler;
    }

    /**
     * 唤醒（不带提示）
     *
     * @param event 唤醒时传入的事件
     */
    public void notify(AMEvent event) {
        notify(event, null);
    }

    /**
     * 唤醒并尝试打印信息
     *
     * @param event 唤醒时传入的事件
     * @param msg   传入的提示
     */
    public void notify(AMEvent event, String msg) {
        if (isNull || event.getIndex() == AMEvent.ERR_UNEXPECTED.getIndex() && executor.isShutdown())
            return;
        if (handler != null)
            executor.execute(() -> handler.handle(event, msg));
        else if (msg != null && printHint)
            System.out.println("Notifier:" + msg);
        if (event.getIndex() >= AMEvent.ERR_AT_NOT_FOUND.getIndex())
            executor.shutdown();
    }

    /**
     * 尝试关闭线程池
     *
     * @return true - 成功关闭, false - 已关闭
     */
    public boolean shutdown() {
        if (isNull || executor.isShutdown())
            return false;
        executor.shutdown();
        return true;
    }
}
