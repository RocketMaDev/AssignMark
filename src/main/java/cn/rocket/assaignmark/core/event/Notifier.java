package cn.rocket.assaignmark.core.event;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class Notifier {
    private final AMEventHandler handler;

    public Notifier(AMEventHandler handler) {
        this.handler = handler;
    }

    // TODO ThreadPool
    public synchronized void notify(AMEvent event) {
        notify(event, null);
    }

    public synchronized void notify(AMEvent event, String msg) {
        if (handler != null)
            new Thread(() -> handler.handle(event, msg), "AMNotifier Thread").start();//TODO thread name
    }
}
