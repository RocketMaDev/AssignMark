package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import cn.rocket.assaignmark.core.exception.AssigningException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class AMFactory {
    public static final String TEMPLATE_PATH = "/amres/core/template.xlsx";
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

    public static void extractTable(String outputPath) throws IOException {
        try (InputStream is = AMFactory.class.getResourceAsStream(TEMPLATE_PATH)) {
            assert is != null;
            Files.copy(is, Paths.get(outputPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void work() {
        try {
            impl_work();
        } catch (AssigningException ignored) {
        } catch (Exception e) {
            notifier.notify(AMEvent.ERROR_UNEXPECTED, e.toString());
            e.printStackTrace();
        }
    }

    public void impl_work() throws AssigningException {
        AssigningTable at = new AssigningTable(assigningTablePath, handler, notifier);
        at.checkAndLoad();
        MarkTable mt = new MarkTable(markTablePath, handler, outputPath, at, notifier);
        mt.checkAndLoad();
        mt.calcAssignedMarks();
    }
}