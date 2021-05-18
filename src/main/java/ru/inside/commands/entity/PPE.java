package ru.inside.commands.entity;

import com.sun.istack.NotNull;
import lombok.*;
import ru.inside.commands.entity.enums.PPEStatus;

import javax.annotation.concurrent.Immutable;
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
    @Column(columnDefinition = "TEXT", unique = true)
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
    @Column(columnDefinition = "bigint")
    private Duration lifeTime;

    /**
     * Поле ppeStatus - статус средства индивидуальной защиты
     */
    @Column
    private PPEStatus ppeStatus;


    @ManyToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "employee_ppe",
            joinColumns = @JoinColumn(name = "ppe_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"))
//    @Column(updatable = false) //TODO: check if writed to db and field is immutable
    private Employee employee;
}
