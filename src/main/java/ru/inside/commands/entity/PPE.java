package ru.inside.commands.entity;

import com.sun.istack.NotNull;
import lombok.*;
import ru.inside.commands.entity.enums.PPEStatus;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PPE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название средства индивидуальной защиты
     */
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String name;

    /**
     * Цена СИЗа
     */
    @Column
    private Float price;

    /**
     * Поле inventoryNumber - инвентарный номер СИЗа
     */
    @Column(columnDefinition = "TEXT")
    private String inventoryNumber;

    /**
     * Поле startUseDate - дата поступления СИЗа в эксплуатацию
     * Желательно использовать время по Москве
     */
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startUseDate;

    /**
     * Поле lifeTime - cрок службы
     */
    @Column(columnDefinition = "interval")
    private Duration lifeTime;

    /**
     * Поле ppeStatus - статус средства индивидуальной защиты
     */
    @Column
    private PPEStatus ppeStatus;

    @MapsId
    @OneToOne
    private Employee employee;
}
