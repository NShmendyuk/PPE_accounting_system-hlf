package ru.inside.commands.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * Поле personnelNumber - табельный номер сотрудника
     */
    @Column(unique = true)
    @NotNull
    private String personnelNumber;

    /**
     * Поле occupation - должность сотрудника
     */
    @Column
    private String occupation;

    /**
     * Поле ppe - средства индивидуальной защиты, закрепленные за сотрудником
     */
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "employee_ppe",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ppe_id", referencedColumnName = "id"))
    private List<PPE> ppe;

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

    @Override
    public String toString() {
        return "[Employee:{" + this.employeeName + "; " + this.personnelNumber + "; " + this.occupation + "}]";
    }
}
