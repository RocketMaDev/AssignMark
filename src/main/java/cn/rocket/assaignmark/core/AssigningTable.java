package cn.rocket.assaignmark.core;

import cn.rocket.assaignmark.LocalURL;
import cn.rocket.assaignmark.core.event.AMEvent;
import cn.rocket.assaignmark.core.event.AMEventHandler;
import cn.rocket.assaignmark.core.event.Notifier;
import cn.rocket.assaignmark.core.exception.AssigningException;
import cn.rocket.assaignmark.core.exception.IncorrectSheetException;
import cn.rocket.assaignmark.core.exception.InvalidTableException;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
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

import static cn.rocket.assaignmark.core.AMFactory.attachUnclosedEvent;
import static cn.rocket.assaignmark.core.AMFactory.getExceptionStack;

/**
 * 赋分表类
 * 用来存储赋分比例信息
 *
 * @author Rocket
 * @version 1.0.8
 * @since 0.9.8
 */
public class AssigningTable {
    public static final int SUBJECTS = 7;
    public static final int STAGES = 20;

    public static final String[] SUBJECT_NAMES = {"政治", "历史", "地理", "物理", "化学", "生物", "技术"};
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
    private final XSSFWorkbook wb;
    private final Notifier notifier;
    private double[][] allReqrStageNums;
    private boolean[] isRatios;

    private final Thread thisThread = Thread.currentThread();

    /**
     * 构造一个赋分表实例，并允许使用给定的<code>notifier</code>，前提是继承该类。
     * 若只是将其与分数表两两捆绑使用，请考虑<code>AMFactory</code>
     * <p>
     * 所有参数基本不可为<code>null</code>
     * <p>
     * <i>这儿的水很深，你把握不住。</i>
     *
     * @param wbPath    赋分表路径
     * @param handler   <code>AMEvent</code>事件处理器实例，<i>此项可为<code>null</code></i>
     * @param _notifier 事件唤醒器实例
     * @param parent    指定上述路径的父路径（若为相对路径），<code>null</code>表示为jar所在路径
     * @throws AssigningException 如果赋分表无法加载
     * @see AMFactory
     * @see AMEvent
     * @see AMEventHandler
     * @see LocalURL#JAR_PARENT_PATH
     */
    protected AssigningTable(String wbPath, AMEventHandler handler, Notifier _notifier, String parent)
            throws AssigningException, InterruptedException {
        if (_notifier != null)
            notifier = _notifier;
        else
            notifier = new Notifier(handler);
        notifier.notify(AMEvent.LOAD_AT);
        String realParent = parent == null ? LocalURL.JAR_PARENT_PATH : parent;
        try {
            if (AMFactory.getFile(realParent, wbPath)
                    .length() > 1024 * 1024) { // 1MiB
                notifier.notify(AMEvent.ERR_INVALID_AT);
                throw new AssigningException();
            }
            OPCPackage pkg = OPCPackage.open(new FileInputStream(AMFactory.getFile(realParent, wbPath)));
            wb = new XSSFWorkbook(pkg);
        } catch (OLE2NotOfficeXmlFileException | EmptyFileException | InvalidFormatException e) {
            notifier.notify(AMEvent.ERR_AT_INVALID_FORMAT);
            throw new AssigningException(e);
        } catch (FileNotFoundException e) {
            notifier.notify(AMEvent.ERR_AT_NOT_FOUND);
            throw new AssigningException(e);
        } catch (IOException e) {
            notifier.notify(AMEvent.ERR_READING_AT);
            throw new AssigningException(e);
        }
        assigningSheet = wb.getSheetAt(0);

        if (thisThread.isInterrupted())
            interrupt(true);
    }

    /**
     * 构造一个赋分表实例，如果你想单独使用它的话，或想获得对其生命周期的完全控制。
     * <p>
     * <b>切记使用完毕后调用<code>shutdownNotifier()</code>来关闭线程池</b>
     * <p>
     * 所有参数基本不可为<code>null</code>
     *
     * @param wbPath  赋分表路径
     * @param handler <code>AMEvent</code>事件处理器实例，<i>此项可为<code>null</code></i>
     * @param parent  指定上述路径的父路径（若为相对路径），<code>null</code>表示为jar所在路径
     * @throws AssigningException 如果赋分表无法加载
     * @see AMEvent
     * @see AMEventHandler
     * @see LocalURL#JAR_PARENT_PATH
     */
    public AssigningTable(String wbPath, AMEventHandler handler, String parent) throws AssigningException, InterruptedException {
        this(wbPath, handler, null, parent);
    }

    /**
     * 检查并加载赋分比例
     *
     * @throws AssigningException 在检查读取赋分比例时出现了异常
     */
    public void checkAndLoad() throws AssigningException, InterruptedException {
        if (allReqrStageNums != null)
            return;
        notifier.notify(AMEvent.CHECK_AT);
        isRatios = new boolean[SUBJECTS];
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
            IOException ioe = null;
            try {
                wb.close();
            } catch (IOException ee) {
                ioe = ee;
            }
            notifier.notify(AMEvent.ERR_INVALID_AT, ioe == null ? null : attachUnclosedEvent(ioe));
            throw e;
        }


        // 读取赋分比例?人数?
        Row boolRow = assigningSheet.getRow(BOOL_ROW);
        for (int i = 0; i < SUBJECTS; i++) {
            c = boolRow.getCell(i + MARK_COL_START, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            if (c != null && c.getCellType().equals(CellType.BOOLEAN))
                isRatios[i] = c.getBooleanCellValue();
            else
                try {
                    throw new IncorrectSheetException();
                } catch (IncorrectSheetException e) {
                    IOException ioe = null;
                    try {
                        wb.close();
                    } catch (IOException ee) {
                        ioe = ee;
                    }
                    notifier.notify(AMEvent.ERR_AT_INCORRECT_FORMAT, ioe == null ? null : attachUnclosedEvent(ioe));
                    throw e;
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
                    if (c == null || !c.getCellType().equals(CellType.NUMERIC))
                        allReqrStageNums[i][r] = 0;
                    else {
                        t = Double.parseDouble(formatter.formatCellValue(c));
                        if (t > -1 && t < 0 || t < -1) // 处理负数
                            throw new IncorrectSheetException();
                        allReqrStageNums[i][r] = t;
                    }
                }
        } catch (IncorrectSheetException | NumberFormatException e) {
            IOException ioe = null;
            try {
                wb.close();
            } catch (IOException ee) {
                ioe = ee;
            }
            notifier.notify(AMEvent.ERR_AT_INCORRECT_FORMAT, ioe == null ? null : attachUnclosedEvent(ioe));
            throw new AssigningException(e);
        }
        try {
            wb.close();
        } catch (IOException e) {
            notifier.notify(AMEvent.ERR_FAILED_TO_CLOSE, getExceptionStack(e));
            throw new AssigningException(e);
        }

        if (thisThread.isInterrupted())
            interrupt(false);
    }

    /**
     * 请使用此方法结束线程池
     * <p>
     * 请注意如果{@link AssigningTable}和{@link MarkTable}共享同一<code>notifier</code>，其会在执行完<code>calcAssignedMarks()</code>
     * 后自动关闭
     *
     * @return true - 成功关闭, false - 已关闭
     * @see MarkTable#calcAssignedMarks()
     */
    public boolean shutdownNotifier() {
        return notifier.shutdown();
    }

    /**
     * 获取对应科目的赋分分段实际人数
     *
     * @param subject      科目
     * @param validPersons 有效人数（当赋分表内是人数而非比例时，该数无效
     * @return <code>int[]</code> - 选定科目各个赋分分段实际人数
     */
    int[] getReqrStageNums(int subject, int validPersons) {
        if (subject < 0 || subject > SUBJECTS)
            throw new RuntimeException("var subject should in the range");
        if (allReqrStageNums == null)
            throw new NullPointerException("please first run checkAndLoad() before invoke this method.");
        double[] in = allReqrStageNums[subject];
        int[] out = new int[STAGES];
        if (isRatios[subject])
            for (int i = 0; i < STAGES; i++)
                out[i] = Math.toIntExact(in[i] == -1.0 ? -1 : Math.round(in[i] * validPersons));
        else
            for (int i = 0; i < STAGES; i++)
                out[i] = (int) in[i];

        return out;
    }

    /**
     * 检查该赋分表实例是否已加载完毕
     *
     * @return 是否加载完毕
     */
    boolean isNotLoaded() {
        return allReqrStageNums == null;
    }

    /**
     * 处理线程中断
     *
     * @param closeWb 是否关闭<code>workbook</code>
     * @throws InterruptedException 始终抛出
     */
    private void interrupt(boolean closeWb) throws InterruptedException {
        if (closeWb) {
            try {
                wb.close();
            } catch (IOException e) {
                notifier.notify(AMEvent.ERR_INTERRUPTED, attachUnclosedEvent(e));
                throw new InterruptedException();
            }
        }
        notifier.notify(AMEvent.ERR_INTERRUPTED);
        throw new InterruptedException();
    }
}
