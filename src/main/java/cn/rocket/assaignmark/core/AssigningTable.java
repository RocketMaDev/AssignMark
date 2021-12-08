package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class AssigningTable {
    public static final int SUBJECTS = 7;
    public static final int STAGES = 20;

    public static final int POLITICS = 0;
    public static final int HISTORY = 1;
    public static final int GEOGRAPHY = 2;
    public static final int PHYSICS = 3;
    public static final int CHEMISTRY = 4;
    public static final int BIOLOGY = 5;
    public static final int TECHNOLOGY = 6;

    public static final int BOOL_ROW = 1;
    public static final int MARK_ROW_START = 2;
    public static final int MARK_COL_START = 1;

    private final XSSFSheet assigningSheet;
    private XSSFWorkbook wb;
    private final Notifier notifier;
    private double[][] allReqrStageNums;
    private boolean[] isIntegers;

    public AssigningTable(String wbPath, AMEventHandler handler) {
        notifier = new Notifier(handler);
        notifier.notify(AMEvent.LOAD_AT);
        try {
            OPCPackage pkg = OPCPackage.open(new FileInputStream(wbPath));
            wb = new XSSFWorkbook(pkg);
        } catch (InvalidFormatException e) {
            notifier.notify(AMEvent.ERROR_AT_INCORRECT_FORMAT);
        } catch (FileNotFoundException e) {
            notifier.notify(AMEvent.ERROR_AT_NOT_FOUND);
        } catch (IOException e) {
            notifier.notify(AMEvent.ERROR_READING_AT);
        }
        assigningSheet = wb.getSheetAt(0);
    }

    public void checkAndLoad() {
        if (allReqrStageNums != null)
            return;
        notifier.notify(AMEvent.CHECK_AT);
        isIntegers = new boolean[STAGES];
        allReqrStageNums = new double[SUBJECTS][STAGES];
        DataFormatter formatter = new DataFormatter();
        Row boolRow = assigningSheet.getRow(BOOL_ROW);
        String s;
        for (int i = 0; i < SUBJECTS; i++) {
            s = formatter.formatCellValue(boolRow.getCell(i,
                    Row.MissingCellPolicy.RETURN_NULL_AND_BLANK));
            isIntegers[i] = Boolean.parseBoolean(s);
        }
        Row[] rows = new Row[STAGES];
        Cell c;
        for (int r = 0; r < STAGES; r++)
            rows[r] = assigningSheet.getRow(MARK_ROW_START + r);
        for (int i = 0; i < SUBJECTS; i++)
            for (int r = 0; r < STAGES; r++) {
                c = rows[r].getCell(MARK_COL_START + i, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                allReqrStageNums[i][r] =
                        c.getCellType().equals(CellType.NUMERIC) ? Double.parseDouble(formatter.formatCellValue(c)) : 0;
            }
        try {
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int[] getReqrStageNums(int subject, int validPersons) {
        if (subject < 0 || subject > SUBJECTS)
            throw new RuntimeException("var subject should in the range");
        if (allReqrStageNums[subject] == null)
            throw new NullPointerException("please first run checkAndLoad() before invoke this method.");
        double[] in = allReqrStageNums[subject];
        int[] out = new int[STAGES];
        if (isIntegers[subject])
            for (int i = 0; i < STAGES; i++)
                out[i] = (int) in[i];
        else
            for (int i = 0; i < STAGES; i++)
                out[i] = Math.toIntExact(in[i] == -1.0 ? -1 : Math.round(in[i] * validPersons));
        return out;
    }

    //TODO 输入时负数的处理
}
