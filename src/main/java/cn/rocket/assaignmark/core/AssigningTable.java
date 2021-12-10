package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import cn.rocket.assaignmark.core.exception.IncorrectSheetException;
import cn.rocket.assaignmark.core.exception.InvalidTableException;
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

    AssigningTable(String wbPath, AMEventHandler handler, Notifier notifier) {
        if (notifier != null)
            this.notifier = notifier;
        else
            this.notifier = new Notifier(handler);
        this.notifier.notify(AMEvent.LOAD_AT);
        try {
            OPCPackage pkg = OPCPackage.open(new FileInputStream(wbPath));
            wb = new XSSFWorkbook(pkg);
        } catch (InvalidFormatException e) {
            this.notifier.notify(AMEvent.ERROR_AT_INCORRECT_FORMAT);
        } catch (FileNotFoundException e) {
            this.notifier.notify(AMEvent.ERROR_AT_NOT_FOUND);
        } catch (IOException e) {
            this.notifier.notify(AMEvent.ERROR_READING_AT);
        }
        assigningSheet = wb.getSheetAt(0);
    }

    public AssigningTable(String wbPath, AMEventHandler handler) {
        this(wbPath, handler, null);
    }

    public void checkAndLoad() {
        if (allReqrStageNums != null)
            return;
        notifier.notify(AMEvent.CHECK_AT);
        isIntegers = new boolean[STAGES];
        allReqrStageNums = new double[SUBJECTS][STAGES];
        DataFormatter formatter = new DataFormatter();
        Cell c;
        double t;

        // 检验有效赋分表
        try {
            Row initialRow = assigningSheet.getRow(0);
            if (initialRow == null)
                throw new InvalidTableException();
            c = initialRow.getCell(0);
            if (c == null || !formatter.formatCellValue(c).equals("Rocket"))
                throw new InvalidTableException();
        } catch (InvalidTableException e) {
            notifier.notify(AMEvent.ERROR_INVALID_AT);
            try {
                wb.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


        // 读取赋分比例?人数?
        Row boolRow = assigningSheet.getRow(BOOL_ROW);
        String s;
        for (int i = 0; i < SUBJECTS; i++) {
            c = boolRow.getCell(i + MARK_COL_START, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            if (c.getCellType().equals(CellType.BOOLEAN))
                isIntegers[i] = c.getBooleanCellValue();
            else
                try {
                    throw new IncorrectSheetException();
                } catch (IncorrectSheetException e) {
                    notifier.notify(AMEvent.ERROR_AT_INVALID_FORMAT);
                    try {
                        wb.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
        }

        // 读取各分段人数/比例
        Row[] rows = new Row[STAGES];
        for (int r = 0; r < STAGES; r++)
            rows[r] = assigningSheet.getRow(MARK_ROW_START + r);
        try {
            for (int i = 0; i < SUBJECTS; i++)
                for (int r = 0; r < STAGES; r++) {
                    c = rows[r].getCell(MARK_COL_START + i, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
                    if (!c.getCellType().equals(CellType.NUMERIC))
                        allReqrStageNums[i][r] = 0;
                    else {
                        t = Double.parseDouble(formatter.formatCellValue(c));
                        if (t > -1 && t < 0) //处理负数
                            throw new IncorrectSheetException();
                        allReqrStageNums[i][r] = t;
                    }
                }
        } catch (IncorrectSheetException | NumberFormatException e) {
            notifier.notify(AMEvent.ERROR_AT_INCORRECT_FORMAT);
        } finally {
            try {
                wb.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    int[] getReqrStageNums(int subject, int validPersons) {
        if (subject < 0 || subject > SUBJECTS)
            throw new RuntimeException("var subject should in the range");
        if (allReqrStageNums == null)
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

    boolean isNotLoaded() {
        return allReqrStageNums == null;
    }
}
