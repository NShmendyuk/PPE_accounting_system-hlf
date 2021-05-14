package ru.inside.commands.entity.forms;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeForm {
    String employeeName;
    String personnelNumber;
    String occupation;
}
