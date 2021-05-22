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
public class Subsidiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Поле name - название дочернего общества
     */
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String name;

    /**
     * Поле peerName - название дочернего общества в сети блокчейна
     */
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String peerName;

    /**
     * Поле employee - сотрудники дочернего общества
     */
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "subsidiary_employee",
            joinColumns = @JoinColumn(name = "subsidiary_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"))
    private List<Employee> employee;

    @Override
    public String toString() {
        return "[Subsidiary:{" + this.name + "; " + this.peerName + "}]";
    }
}
