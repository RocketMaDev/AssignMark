package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import cn.rocket.assaignmark.core.exception.IncorrectSheetException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static cn.rocket.assaignmark.core.AssigningTable.*;

/**
 * @author Rocket
 * @version 0.9-pre
 */
public class MarkTable {
    public static final int ROW_LIMIT = 10;
    public static final int COL_LIMIT = 5;
    public static final int VALID_PERSONS = 0;
    public static final int MARK_COL = 1;
    public static final int ASSIGNING_COL = 2;
    public static final int START_ROW = 3;
    public static final int INFO_COUNT = 4;

    private double[][] allMarks;
    private XSSFSheet[] markSheets;
    private int[][] allSheetInfos;
    private XSSFWorkbook markWorkbook;
    private final Notifier notifier;
    private final String outputPath;
    private final AssigningTable assigningTable;

    public MarkTable(String wbPath, AMEventHandler handler, String outputPath, AssigningTable assigningTable) {
        if (assigningTable == null)
            throw new NullPointerException("assigningTable不能为null!");
        this.assigningTable = assigningTable;
        this.outputPath = outputPath;
        notifier = new Notifier(handler);
        notifier.notify(AMEvent.LOAD_MT);
        try {
            OPCPackage pkg = OPCPackage.open(new FileInputStream(wbPath));
            markWorkbook = new XSSFWorkbook(pkg);
        } catch (FileNotFoundException e) {
            notifier.notify(AMEvent.ERROR_MT_NOT_FOUND);
        } catch (InvalidFormatException e) {
            notifier.notify(AMEvent.ERROR_READING_AT);
        } catch (IOException e) {
            notifier.notify(AMEvent.ERROR_READING_MT);
        }
    }

    /**
     * flag:1:只找到原分，2:只找到赋分
     *
     * @param sheet   查找的表格
     * @param subject 表格对应的科目
     */
    private void readSheetInfos(Sheet sheet, int subject) throws IncorrectSheetException {
        int flag = 0;
        int[] infos = allSheetInfos[subject];
        DataFormatter formatter = new DataFormatter();

        int offset = 0, hp = 0, vp = 0;
        Row row;
        Cell c;
        String s;
        for (int r = 0; r < ROW_LIMIT && flag != 3; r++) {
            row = sheet.getRow(r);
            if (row == null)
                continue;
            while (offset < COL_LIMIT && flag != 3) {
                c = row.getCell(hp + offset);
                if (c == null) {
                    offset++;
                    continue;
                }
                hp += offset;
                offset = 0;
                s = formatter.formatCellValue(c);
                if (s.equals("原分") && flag != 1) {
                    flag++;
                    infos[MARK_COL] = hp;
                    vp = r;
                } else if (s.equals("赋分") && flag != 2) {
                    infos[ASSIGNING_COL] = hp;
                    flag += 2;
                }
                offset++;
            }
        }
        if (flag != -3)
            throw new IncorrectSheetException("找不到赋分或原分栏");
        hp = infos[MARK_COL];
        for (int i = vp + 1; i < vp; i++) {
            row = sheet.getRow(i);
            if (row == null)
                continue;
            c = row.getCell(hp);
            if (c != null && c.getCellType().equals(CellType.NUMERIC)) {
                infos[START_ROW] = i;
                break;
            }
        }
        if (infos[START_ROW] == 0)
            throw new IncorrectSheetException("找不到原分");

        vp = infos[START_ROW];
        hp = infos[MARK_COL];
        do {
            vp++;
            row = sheet.getRow(vp);
            if (row == null)
                break;
            c = row.getCell(hp);
        } while (c != null && c.getCellType().equals(CellType.NUMERIC));
        infos[VALID_PERSONS] = vp - infos[START_ROW];
    }

    private void readMarks(Sheet sheet, int subject) throws IncorrectSheetException {
        int[] infos = allSheetInfos[subject];
        double n;
        DataFormatter formatter = new DataFormatter();
        int hp = infos[MARK_COL];
        int start = infos[START_ROW], end = infos[VALID_PERSONS] + start;
        allMarks[subject] = new double[infos[VALID_PERSONS]];
        double[] marks = allMarks[subject];
        Cell c;

        try {
            for (int i = start; i < end; i++) {
                c = sheet.getRow(i).getCell(hp);
                n = Double.parseDouble(formatter.formatCellValue(c));
                if (n < 0) {
                    throw new IncorrectSheetException("分数小于0");
                }
                marks[i - start] = n;
            }
        } catch (NumberFormatException e) {
            throw new IncorrectSheetException("非分数", e);
        }
    }

    public void checkAndLoad() {
        notifier.notify(AMEvent.LOAD_MT);
        allMarks = new double[SUBJECTS][];
        markSheets = new XSSFSheet[SUBJECTS];
        allSheetInfos = new int[SUBJECTS][INFO_COUNT];
        for (XSSFSheet sheet : markSheets)
            // 若重复出现多个重名工作表，以最后一个为准
            switch (sheet.getSheetName()) {
                case "政治":
                    markSheets[POLITICS] = sheet;
                    break;
                case "历史":
                    markSheets[HISTORY] = sheet;
                    break;
                case "地理":
                    markSheets[GEOGRAPHY] = sheet;
                    break;
                case "物理":
                    markSheets[PHYSICS] = sheet;
                    break;
                case "化学":
                    markSheets[CHEMISTRY] = sheet;
                    break;
                case "生物":
                    markSheets[BIOLOGY] = sheet;
                    break;
                case "技术":
                    markSheets[TECHNOLOGY] = sheet;
                    break;
                default:
                    break;
            }
        for (int i = 0; i < SUBJECTS; i++) {
            if (markSheets[i] == null)
                continue;
            try {
                notifier.notify(AMEvent.getIndexAt(4 + i)); // 4:ASSIGN_POLITICS.index
                readSheetInfos(markSheets[i], i);
                readMarks(markSheets[i], i);
            } catch (IncorrectSheetException e) {
                try {
                    markWorkbook.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                notifier.notify(AMEvent.ERROR_MT_INCORRECT_FORMAT);
            }
        }
    }

    private void writeAssignedMarks(int subject, int[] assignedMarks) {
        Sheet sheet = markSheets[subject];
        int hp = allSheetInfos[subject][ASSIGNING_COL];
        int vp = allSheetInfos[subject][START_ROW];
        int length = allSheetInfos[subject][VALID_PERSONS];
        Row row;
        for (int i = 0; i < length; i++) {
            row = sheet.getRow(i + vp);
            row.getCell(hp, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).setCellValue(assignedMarks[i]);
        }
    }

    public void calcAssignedMarks() {
        boolean allEmpty = true;
        for (int i = 0; i < SUBJECTS; i++)
            if (markSheets[i] != null) {
                allEmpty = false;
                break;
            }
        if (allEmpty) {
            try {
                markWorkbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            notifier.notify(AMEvent.ERROR_MT_INCORRECT_FORMAT);
        }
        for (int i = 0; i < SUBJECTS; i++) {
            if (markSheets[i] == null)
                continue;
            SingleMarkTable smt = new SingleMarkTable(allMarks[i], assigningTable.getReqrStageNums(
                    i, allSheetInfos[i][VALID_PERSONS]));
            smt.searchStages();
            int[] assignedMarks = smt.assignMark();
            writeAssignedMarks(i, assignedMarks);
        }
        notifier.notify(AMEvent.WRITE_OUT);
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            markWorkbook.write(out);
        } catch (FileNotFoundException e) {
            notifier.notify(AMEvent.ERROR_INVALID_OUTPUT_PATH);
        } catch (IOException e) {
            notifier.notify(AMEvent.ERROR_FAILED_TO_WRITE);
        } finally {
            try {
                markWorkbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notifier.notify(AMEvent.DONE);
    }
}
