package ru.inside.commands.entity.enums;

public enum EmployeeStatus {
    WORKING,
    DISMISSED,
    TRANSFERED;

    EmployeeStatus() {
    }

    @Override
    public String toString() {
        switch(this) {
            case WORKING: return "РАБОТАЕТ";
            case DISMISSED: return "УВОЛЕН";
            case TRANSFERED: return "ПЕРЕВЕДЕН";
            default: return "НЕИЗВЕСТНО";
        }
    }
}
