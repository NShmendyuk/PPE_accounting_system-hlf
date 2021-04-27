package ru.inside.commands.entity.enums;

public enum SubsidiaryStatus {
    ACCESSED,
    UNACCESSED;

    SubsidiaryStatus() {
    }

    @Override
    public String toString() {
        switch(this) {
            case ACCESSED: return "ДОСТУПЕН";
            case UNACCESSED: return "НЕДОСТУПЕН";
            default: return "НЕТ ДАННЫХ";
        }
    }
}
