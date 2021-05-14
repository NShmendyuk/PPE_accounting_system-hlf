package ru.inside.commands.hyperledger.entity;

import lombok.Data;

@Data
public class PPEContract {
    /**
     * Инвентарный номер СИЗа
     */
    private String inventoryNumber;

    /**
     * Фамилия, имя, отчество сотрудника
     */
    private String ownerName;

    /**
     * Табельный номер сотрудника
     */
    private String ownerID;

    /**
     * Название средства индивидуальной защиты
     */
    private String name;

    /**
     * Цена СИЗа
     */
    private Float price;

    /**
     * Дата поступления СИЗа в эксплуатацию
     */
    private String startUseDate;

    /**
     * Срок службы
     */
    private Integer lifeTime;

    /**
     * Организация
     */
    private String subsidiary;
}
