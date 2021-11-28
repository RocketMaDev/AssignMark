package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEventHandler;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class AMFactory {
    private final String assigningTablePath;
    private final String markTablePath;
    private final AMEventHandler handler;
    private final String outputPath;

    public AMFactory(String assigningTablePath, String markTablePath, AMEventHandler handler, String outputPath) {
        this.assigningTablePath = assigningTablePath;
        this.markTablePath = markTablePath;
        this.handler = handler;
        this.outputPath = outputPath;
    }

    public void work() {
        AssigningTable at = new AssigningTable(assigningTablePath, handler);
        at.checkAndLoad();
        MarkTable mt = new MarkTable(markTablePath, handler, outputPath, at);
        mt.checkAndLoad();
        mt.calcAssignedMarks();
    }
}
