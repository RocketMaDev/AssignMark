package cn.rocket.assaignmark.core.event;

/**
 * @author Rocket
 * @version 0.9-pre
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
    ERR_INVALID_OUTPUT_PATH(41),
    ERR_FAILED_TO_WRITE(42),
    ERR_MT_EMPTY(43),
    ERR_UNEXPECTED(44);

    private final int index;

    AMEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static AMEvent getIndexAt(int index) {
        if (index < 0 || index > ERR_UNEXPECTED.index || index > DONE.index && index < ERR_AT_NOT_FOUND.index)
            throw new IllegalArgumentException("未知索引");
        if (index <= DONE.index)
            return AMEvent.values()[index];
        else
            return AMEvent.values()[index - ERR_AT_NOT_FOUND.index + DONE.index + 1];
    }
}
