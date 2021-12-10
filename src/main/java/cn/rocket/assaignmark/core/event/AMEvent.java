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

    ERROR_AT_NOT_FOUND(32),
    ERROR_READING_AT(33),
    ERROR_AT_INVALID_FORMAT(34),
    ERROR_AT_INCORRECT_FORMAT(35),
    ERROR_INVALID_AT(36),
    ERROR_MT_NOT_FOUND(37),
    ERROR_READING_MT(38),
    ERROR_MT_INVALID_FORMAT(39),
    ERROR_MT_INCORRECT_FORMAT(40),
    ERROR_INVALID_OUTPUT_PATH(41),
    ERROR_FAILED_TO_WRITE(42),
    ERROR_UNEXPECTED(43);

    private final int index;

    AMEvent(int index) {
        this.index = index;
    }

    public int getIndex(AMEvent event) {
        return event.index;
    }

    public static AMEvent getIndexAt(int index) {
        if (index < 0 || index > ERROR_UNEXPECTED.index || index > DONE.index && index < ERROR_AT_NOT_FOUND.index)
            throw new IllegalArgumentException("未知索引");
        if (index <= DONE.index)
            return AMEvent.values()[index];
        else
            return AMEvent.values()[index - ERROR_AT_NOT_FOUND.index + DONE.index + 1];
    }
}
