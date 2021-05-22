package ru.inside.commands.hyperledger.entity;

import lombok.Data;

@Data
public class PPEContract {
    /**
     * Инвентарный номер средства индивидуальной защиты
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
     * Статус средства индивидуальной защиты
     */
    private String status;

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
