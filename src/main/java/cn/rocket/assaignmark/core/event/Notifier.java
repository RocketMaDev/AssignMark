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

    public synchronized void notify(AMEvent event) {
        if (handler != null)
            new Thread(() -> handler.handle(event), "AMNotifier Thread").start();//TODO thread name
    }
}
