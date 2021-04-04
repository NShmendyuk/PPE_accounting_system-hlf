package ru.inside.commands.entity.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class EmployeeDto implements Serializable {
    private static final long serialVersionUID = -8930070477674273629L;
    private Long id;
    private Long ppeId;
    private Long subsidiaryId;
    private String employeeName;
    private Long employeeID;
    private String occupation;
}
