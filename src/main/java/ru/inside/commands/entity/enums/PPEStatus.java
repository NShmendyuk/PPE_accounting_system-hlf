package ru.inside.commands.entity.enums;

public enum PPEStatus {
    APPLIED,
    TRANSFER,
    COMMISSIONED,
    DECOMMISSIONED,
    SPOILED;

    PPEStatus() {
    }

    @Override
    public String toString() {
        switch(this) {
            case APPLIED: return "Новый";
            case TRANSFER: return "Передача в другое ДО";
            case COMMISSIONED: return "Введен в эксплуатацию";
            case DECOMMISSIONED: return "Списан";
            case SPOILED: return "Испорчен";
            default: return "НЕИЗВЕСТНО";
        }
    }
}
