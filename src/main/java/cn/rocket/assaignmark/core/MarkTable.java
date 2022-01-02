package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import cn.rocket.assaignmark.core.exception.AssigningException;
import cn.rocket.assaignmark.core.exception.EmptyMarkTableException;
import cn.rocket.assaignmark.core.exception.IncorrectSheetException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

import static cn.rocket.assaignmark.core.AssigningTable.*;

/**
 * 分数表类
 * 用来进行赋分
 *
 * @author Rocket
 * @version 0.9.8
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
    private Sheet[] markSheets;
    private int[][] allSheetInfos;
    private final XSSFWorkbook markWorkbook;
    private final Notifier notifier;
    private final String outputPath;
    private final AssigningTable assigningTable;

    /**
     * 构造一个分数表实例，并允许使用给定的<code>notifier</code>，前提是继承该类。
     * 若只是将其与赋分表两两捆绑使用，请考虑<code>AMFactory</code>
     * <p>
     * 所有参数基本不可为<code>null</code>
     * <p>
     * <i>这儿的水很深，你把握不住。</i>
     *
     * @param wbPath         分数表路径
     * @param handler        <code>AMEvent</code>事件处理器实例，<i>此项可为<code>null</code></i>
     * @param outputPath     输出赋分完毕的分数表路径
     * @param assigningTable 赋分表实例
     * @param _notifier      事件唤醒器实例
     * @throws AssigningException 如果分数表无法加载
     * @see AMFactory
     * @see AMEvent
     * @see AMEventHandler
     */
    protected MarkTable(String wbPath, AMEventHandler handler, String outputPath, AssigningTable assigningTable, Notifier _notifier) throws AssigningException {
        if (_notifier != null)
            notifier = _notifier;
        else
            notifier = new Notifier(handler);
        if (assigningTable == null || assigningTable.isNotLoaded())
            throw new NullPointerException("assigningTable can not null or empty!");
        this.assigningTable = assigningTable;
        this.outputPath = outputPath;
        notifier.notify(AMEvent.LOAD_MT);
        try {
            File wbFile = new File(wbPath);
            OPCPackage pkg;
            // 处理大型xlsx表格
            if (wbFile.length() < 20 * 1024 * 1024) // 20MiB
                pkg = OPCPackage.open(wbFile);
            else
                pkg = OPCPackage.open(new FileInputStream(wbFile));
            markWorkbook = new XSSFWorkbook(pkg);
        } catch (FileNotFoundException e) {
            notifier.notify(AMEvent.ERR_MT_NOT_FOUND);
            throw new AssigningException(e);
        } catch (InvalidFormatException e) {
            notifier.notify(AMEvent.ERR_MT_INVALID_FORMAT);
            throw new AssigningException(e);
        } catch (IOException e) {
            notifier.notify(AMEvent.ERR_READING_MT);
            throw new AssigningException(e);
        }
    }

    /**
     * 构造一个分数表实例，如果你想单独使用它的话。
     * <p>
     * <b>线程池的<code>shutdown()</code>将在赋分完毕的分数表后自动调用</b>
     * <p>
     * 所有参数基本不可为<code>null</code>
     *
     * @param wbPath         分数表路径
     * @param handler        <code>AMEvent</code>事件处理器实例，<i>此项可为<code>null</code></i>
     * @param outputPath     输出赋分完毕的分数表路径
     * @param assigningTable 赋分表实例
     * @throws AssigningException 如果分数表无法加载
     * @see AMEvent
     * @see AMEventHandler
     */
    public MarkTable(String wbPath, AMEventHandler handler, String outputPath, AssigningTable assigningTable) throws AssigningException {
        this(wbPath, handler, outputPath, assigningTable, null);
    }

    /**
     * 读取工作表信息：赋分栏,分数栏,分数起始行,有效人数
     *
     * @param sheet   查找的表格
     * @param subject 表格对应的科目
     * @throws IncorrectSheetException 如果找不到赋分或原分栏或找不到原分起始行
     */
    private void readSheetInfos(Sheet sheet, int subject) throws IncorrectSheetException {
        // flag:1:只找到原分，2:只找到赋分
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
        if (flag != 3)
            throw new IncorrectSheetException("找不到赋分或原分栏");
        hp = infos[MARK_COL];
        for (int i = vp + 1; i < vp + ROW_LIMIT; i++) {
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
            throw new IncorrectSheetException("找不到原分起始行");

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

    /**
     * 读取分数
     *
     * @param sheet   查找的表格
     * @param subject 表格对应的科目
     * @throws IncorrectSheetException 如果非分数或分数小于零
     */
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

    /**
     * 检查分数表并加载分数表信息和分数
     *
     * @throws AssigningException 在检查或读取分数时出现了异常
     * @see MarkTable#readMarks(Sheet, int)
     * @see MarkTable#readSheetInfos(Sheet, int)
     */
    public void checkAndLoad() throws AssigningException {
        if (allMarks != null) {
            return;
        }
        notifier.notify(AMEvent.CHECK_MT);
        allMarks = new double[SUBJECTS][];
        markSheets = new Sheet[SUBJECTS];
        allSheetInfos = new int[SUBJECTS][INFO_COUNT];
        for (Sheet sheet : markWorkbook)
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
                notifier.notify(AMEvent.getIndexAt(AMEvent.ASSIGN_POLITICS.getIndex() + i));
                readSheetInfos(markSheets[i], i);
                readMarks(markSheets[i], i);
            } catch (IncorrectSheetException e) {
                try {
                    markWorkbook.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                notifier.notify(AMEvent.ERR_MT_INCORRECT_FORMAT);
                throw new AssigningException(e);
            }
        }
    }

    /**
     * 写出赋分完毕的单科分数表到内存
     *
     * @param subject       科目
     * @param assignedMarks 赋分完毕的数组
     */
    private void writeAssignedMarks(int subject, int[] assignedMarks) {
        Sheet sheet = markSheets[subject];
        int hp = allSheetInfos[subject][ASSIGNING_COL];
        int vp = allSheetInfos[subject][START_ROW];
        int length = allSheetInfos[subject][VALID_PERSONS];
        Row row;
        for (int i = 0; i < length; i++) {
            row = sheet.getRow(i + vp);
            row.getCell(hp, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(assignedMarks[i]);
        }
    }

    /**
     * 输出赋分完毕的分数表到指定路径
     * <p>
     * 请先执行<code>checkAndLoad()</code>。
     *
     * @throws AssigningException 如果出现IO异常，如文档已打开
     * @see MarkTable#checkAndLoad()
     */
    public void calcAssignedMarks() throws AssigningException {
        if (allMarks == null) {
            throw new NullPointerException("please invoke checkAndLoad() first.");
        }
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
            notifier.notify(AMEvent.ERR_MT_EMPTY);
            throw new AssigningException(new EmptyMarkTableException());
        }
        for (int i = 0; i < SUBJECTS; i++) {
            if (markSheets[i] == null)
                continue;
            SingleMarkTable smt = new SingleMarkTable(allMarks[i], assigningTable.getReqrStageNums(
                    i, allSheetInfos[i][VALID_PERSONS]));
            int[] assignedMarks = smt.assignMark();
            writeAssignedMarks(i, assignedMarks);
        }
        notifier.notify(AMEvent.WRITE_OUT);
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            File f = new File(outputPath);
            if (!f.exists())
                //noinspection ResultOfMethodCallIgnored
                f.createNewFile();
            markWorkbook.write(out);
        } catch (IOException e) {
            notifier.notify(AMEvent.ERR_FAILED_TO_WRITE);
            throw new AssigningException(e);
        } finally {
            try {
                markWorkbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notifier.notify(AMEvent.DONE);
        notifier.shutdown();
    }
}
