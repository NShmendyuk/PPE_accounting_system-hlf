package ru.inside.commands.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Поле employeeName - ФИО сотрудника
     */
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String employeeName;

    /**
     * Поле employeeID - табельный номер сотрудника
     */
    @Column
    @NotNull
    private Long employeeID;

    /**
     * Поле occupation - должность сотрудника
     */
    @Column
    private String occupation;

    /**
     * Поле ppe - средство индивидуальной защиты, закрепленное за сотрудником
     */
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PPE ppe;

    /**
     * Поле subsidiary - дочернее общество в котором находится сотрудник
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    @JoinTable(
            name = "subsidiary_employee",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subsidiary_id", referencedColumnName = "id"))
    private Subsidiary subsidiary;
}
