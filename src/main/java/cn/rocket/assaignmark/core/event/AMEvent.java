package cn.rocket.assaignmark.core.event;

/**
 * 赋分事件枚举
 *
 * @author Rocket
 * @version 1.0.8
 * @since 0.9.8
 */
public enum AMEvent {
    LOAD_AT(0),
    CHECK_AT(1),
    LOAD_MT(2),
    CHECK_MT(3),
    ASSIGN_POLITICS(4),
    ASSIGN_HISTORY(5),
    ASSIGN_GEOGRAPHY(6),
    ASSIGN_PHYSICS(7),
    ASSIGN_CHEMISTRY(8),
    ASSIGN_BIOLOGY(9),
    ASSIGN_TECHNOLOGY(10),
    WRITE_OUT(11),
    DONE(12),

    ERR_AT_NOT_FOUND(32),
    ERR_READING_AT(33),
    ERR_AT_INVALID_FORMAT(34),
    ERR_AT_INCORRECT_FORMAT(35),
    ERR_INVALID_AT(36),
    ERR_MT_NOT_FOUND(37),
    ERR_READING_MT(38),
    ERR_MT_INVALID_FORMAT(39),
    ERR_MT_INCORRECT_FORMAT(40),
    ERR_FAILED_TO_WRITE(41),
    ERR_MT_EMPTY(42),
    ERR_MT_EQUALS_OUT(43),
    ERR_UNEXPECTED(44),
    ERR_INTERRUPTED(45),
    ERR_FAILED_TO_CLOSE(46);

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
            return AMEvent.values()[index];
        else
            return AMEvent.values()[index - ERR_AT_NOT_FOUND.index + DONE.index + 1];
    }

    /**
     * 返回索引
     *
     * @return 当前对象的索引
     */
    public int getIndex() {
        return index;
    }

    public static AMEvent getLastEvent() {
        return AMEvent.values()[AMEvent.values().length - 1];
    }
}
