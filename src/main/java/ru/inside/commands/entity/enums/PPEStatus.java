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
            case APPLIED: return "НОВЫЙ";
            case TRANSFER: return "ПЕРЕДАЧА В ДРУГОЕ ДО";
            case COMMISSIONED: return "ВВЕДЕН В ЭКСПЛУАТАЦИЮ";
            case DECOMMISSIONED: return "СПИСАН";
            case SPOILED: return "ИСПОРЧЕН";
            default: return "НЕИЗВЕСТНОЕ СОСТОЯНИЕ";
        }
    }
}
