package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class AssigningTable {
    public static final int SUBJECTS = 7;
    public static final int STAGES = 21;

    public static final int POLITICS = 0;
    public static final int HISTORY = 1;
    public static final int GEOGRAPHY = 2;
    public static final int PHYSICS = 3;
    public static final int CHEMISTRY = 4;
    public static final int BIOLOGY = 5;
    public static final int TECHNOLOGY = 6;

    private XSSFSheet assigningSheet;
    private XSSFWorkbook wb;
    private Notifier notifier;
    private double[][] allReqrStageNums;
    private boolean[] isIntegers;

    public AssigningTable(String wbPath, AMEventHandler handler) {
        notifier = new Notifier(handler);
        // TODO notifier.notify(0)
        try {
            OPCPackage pkg = OPCPackage.open(new FileInputStream(wbPath));
            wb = new XSSFWorkbook(pkg);
            pkg.close();
        } catch (InvalidFormatException e) {
            //TODO notifier.notify(32)
        } catch (IOException e) {
            //TODO notifier.notify(33)
        }
        assigningSheet = wb.getSheetAt(0);
    }

    public void checkAndLoad() {
        if (allReqrStageNums != null)
            return;
        //TODO notifier.notify(1)
        allReqrStageNums = new double[7][21];
        DataFormatter formatter = new DataFormatter();
        Row boolRow = assigningSheet.getRow(0);//TODO bool row
        String s;
        for (int i = 0; i < 7; i++) {
            s = formatter.formatCellValue(boolRow.getCell(i,
                    Row.MissingCellPolicy.RETURN_NULL_AND_BLANK));
            isIntegers[i] = Boolean.parseBoolean(s);
        }
        Row[] rows = new Row[21];
        Cell t;
    }
}
