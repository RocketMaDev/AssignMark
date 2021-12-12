package cn.rocket.assaignmark.core.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class Notifier {
    private final AMEventHandler handler;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Notifier(AMEventHandler handler) {
        this.handler = handler;
    }

    public void notify(AMEvent event) {
        notify(event, null);
    }

    public void notify(AMEvent event, String msg) {
        if (handler != null)
            executor.execute(() -> handler.handle(event, msg));
        else if (msg != null)
            System.out.println("Notifier:" + msg);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
