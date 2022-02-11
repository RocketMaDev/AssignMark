package cn.rocket.assaignmark.core.event;

/**
 * 赋分事件枚举
 *
 * @author Rocket
 * @version 1.1.8
 * @since 0.9.8
 */
public enum AMEvent {
    // 正常事件
    /**
     * 加载赋分表
     */
    LOAD_AT(0),

    /**
     * 检查赋分表
     */
    CHECK_AT(1),

    /**
     * 加载分数表
     */
    LOAD_MT(2),

    /**
     * 检查分数表
     */
    CHECK_MT(3),

    /**
     * 政治 赋分开始
     */
    ASSIGN_POLITICS(4),

    /**
     * 历史 赋分开始
     */
    ASSIGN_HISTORY(5),

    /**
     * 地理 赋分开始
     */
    ASSIGN_GEOGRAPHY(6),

    /**
     * 物理 赋分开始
     */
    ASSIGN_PHYSICS(7),

    /**
     * 化学 赋分开始
     */
    ASSIGN_CHEMISTRY(8),

    /**
     * 生物 赋分开始
     */
    ASSIGN_BIOLOGY(9),

    /**
     * 技术 赋分开始
     */
    ASSIGN_TECHNOLOGY(10),

    /**
     * 开始写出分数表 事件
     */
    WRITE_OUT(11),

    /**
     * 赋分完成事件
     */
    DONE(12),

    // 异常事件
    /**
     * 未找到赋分表 异常
     */
    ERR_AT_NOT_FOUND(32),

    /**
     * 读取赋分表失败异常
     */
    ERR_READING_AT(33),

    /**
     * 赋分表不是标准xlsx表格异常
     */
    ERR_AT_INVALID_FORMAT(34),

    /**
     * 赋分表不规范异常。请见template.xlsx中的注意事项
     */
    ERR_AT_INCORRECT_FORMAT(35),

    /**
     * 无效赋分表异常。请使用本程序限定的模板
     *
     * @see cn.rocket.assaignmark.core.AMFactory#extractTable(String)
     */
    ERR_INVALID_AT(36),

    /**
     * 分数表未找到异常
     */
    ERR_MT_NOT_FOUND(37),

    /**
     * 读取分数表失败异常
     */
    ERR_READING_MT(38),

    /**
     * 分数表不是xlsx表格异常
     */
    ERR_MT_INVALID_FORMAT(39),

    /**
     * 分数表不规范异常。请见template.xlsx中的注意事项
     */
    ERR_MT_INCORRECT_FORMAT(40),

    /**
     * 无法写出 的异常
     */
    ERR_FAILED_TO_WRITE(41),

    /**
     * 空分数表异常
     */
    ERR_MT_EMPTY(42),

    /**
     * 输出路径和分数表路径相同的异常
     */
    ERR_MT_EQUALS_OUT(43),

    /**
     * 意料之外的异常
     */
    ERR_UNEXPECTED(44),

    /**
     * 线程中断异常
     */
    ERR_INTERRUPTED(45),

    /**
     * 无法关闭文件异常。请注意，此事件<b>只会被附加到其他错误事件上</b>
     *
     * @see cn.rocket.assaignmark.core.AssigningTable
     * @see cn.rocket.assaignmark.core.MarkTable
     */
    ERR_FAILED_TO_CLOSE(46);

    // 方法
    /**
     * 每个事件的索引
     */
    private final int index;

    /**
     * 构造函数。
     *
     * @param index 每个事件的索引
     */
    AMEvent(int index) {
        this.index = index;
    }

    /**
     * 获取对应索引的事件（枚举项）。当没有索引对应的事件时，抛出<code>IllegalArgumentException</code>
     *
     * @param index 需要寻找的索引
     * @return 对应索引的事件（枚举项）
     * @see IllegalArgumentException
     */
    public static AMEvent getIndexAt(int index) {
        if (index < 0 || index > getLastEvent().index || index > DONE.index && index < ERR_AT_NOT_FOUND.index)
            throw new IllegalArgumentException("未知索引");
        if (index <= DONE.index)
            return values()[index];
        else
            return values()[index - ERR_AT_NOT_FOUND.index + DONE.index + 1];
    }

    /**
     * @return 当前对象的索引
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return 最后一个事件
     */
    public static AMEvent getLastEvent() {
        return values()[values().length - 1];
    }
}
