package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class AMFactory {
    private final String assigningTablePath;
    private final String markTablePath;
    private final AMEventHandler handler;
    private final String outputPath;
    private final Notifier notifier;

    public AMFactory(String assigningTablePath, String markTablePath, AMEventHandler handler, String outputPath) {
        this.assigningTablePath = assigningTablePath;
        this.markTablePath = markTablePath;
        this.handler = handler;
        this.outputPath = outputPath;
        this.notifier = new Notifier(handler);
    }

    public static void extractTable(String outputPath) {
        // TODO FileChannel
    }

    public void work() {
        try {
            impl_work();
        } catch (Exception e) {
            notifier.notify(AMEvent.ERROR_UNEXPECTED);
            e.printStackTrace();
        }
    }

    public void impl_work() {
        AssigningTable at = new AssigningTable(assigningTablePath, handler, notifier);
        at.checkAndLoad();
        MarkTable mt = new MarkTable(markTablePath, handler, outputPath, at, notifier);
        mt.checkAndLoad();
        mt.calcAssignedMarks();
    }
}
