package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class MarkTable {
    public static final int VALID_PERSONS = 0;
    public static final int MARK_COL = 1;
    public static final int ASSIGNING_COL = 2;
    public static final int START_ROW = 3;

    private double[][] allMarks;
    private XSSFSheet[] markSheets;
    private int[][] allSheetInfos;
    private XSSFWorkbook markWorkbook;
    private Notifier notifier;
    private String outputPath;

    public MarkTable(String wbPath, AMEventHandler handler, String outputPath) {
        this.outputPath = outputPath;
        notifier = new Notifier(handler);
        //TODO notifier.notify(4)
        try {
            OPCPackage pkg = OPCPackage.open(new FileInputStream(wbPath));
            markWorkbook = new XSSFWorkbook(pkg);
        } catch (InvalidFormatException e) {
            //TODO notifier.notify(34)
        } catch (IOException e) {
            //TODO notifier.notify(35)
        }
    }

    /**
     * flag:1:只找到原分，2:只找到赋分
     *
     * @param sheet
     * @param subject
     */
    private void readSheetInfos(Sheet sheet, int subject) {

    }
}
