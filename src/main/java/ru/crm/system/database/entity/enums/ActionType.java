package ru.crm.system.database.entity.enums;

public enum ActionType {
    CREATED,
    COMMENT,
    CHANGE_ORDER_STATUS,
    SUCCESSFULLY_COMPLETED,
    COMPLETED_UNSUCCESSFULLY,
    TRANSFER_TO_STUDENT,
    SALE_OF_SUBSCRIPTION,
    CANCELLATION_OF_SUBSCRIPTION,
    ADD_MONEY_INTO_STUDENT_BALANCE,
    ADD_MONEY_INTO_TEACHER_BALANCE,
    WRITE_OFF_FROM_STUDENT_BALANCE,
    ATTENDING_A_LESSON,
    MISSING_CLASS,
    ARCHIVED,
    MESSAGE_SENT;
}