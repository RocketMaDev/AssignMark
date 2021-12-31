package cn.rocket.assaignmark.core.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class Notifier {
    private final AMEventHandler handler;
    private final ExecutorService executor;
    private final boolean isNull;

    public Notifier(AMEventHandler handler) {
        executor = handler == null ? null : Executors.newSingleThreadExecutor();
        isNull = handler == null;
        this.handler = handler;
    }

    public void notify(AMEvent event) {
        notify(event, null);
    }

    public void notify(AMEvent event, String msg) {
        if (isNull)
            return;
        if (event.getIndex() == AMEvent.ERR_UNEXPECTED.getIndex() && executor.isShutdown())
            return;
        if (handler != null)
            executor.execute(() -> handler.handle(event, msg));
        else if (msg != null)
            System.out.println("Notifier:" + msg);
        if (event.getIndex() >= AMEvent.ERR_AT_NOT_FOUND.getIndex())
            executor.shutdown();
    }

    public boolean shutdown() {
        if (isNull || executor.isShutdown())
            return false;
        executor.shutdown();
        return true;
    }
}
