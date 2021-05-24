package ru.inside.commands.entity.enums;

public enum PPEStatus {
    APPLIED,
    TRANSFER,
    COMMISSIONED,
    DECOMMISSIONED,
    SPOILED;

    PPEStatus() {
    }

    public static PPEStatus valueOfName(String name) {
        switch(name) {
            case "Новый": return PPEStatus.APPLIED;
            case "в другое ДО": return PPEStatus.TRANSFER;
            case "Введен в эксплуатацию": return PPEStatus.COMMISSIONED;
            case "Списан": return PPEStatus.DECOMMISSIONED;
            case "Испорчен": return PPEStatus.SPOILED;
            default: return PPEStatus.COMMISSIONED;
        }
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
