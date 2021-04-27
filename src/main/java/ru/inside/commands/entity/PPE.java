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
    @Column(columnDefinition = "bigint")
    private Duration lifeTime;

    /**
     * Поле ppeStatus - статус средства индивидуальной защиты
     */
    @Column
    private PPEStatus ppeStatus;


    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    @JoinTable(
            name = "employee_ppe",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ppe_id", referencedColumnName = "id"))
    private Employee employee;
}
