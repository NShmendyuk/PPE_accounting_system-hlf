package ru.inside.commands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inside.commands.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllBySubsidiaryId(Long subsidiaryId);
    Optional<Employee> findByPersonnelNumber(String personnelNumber);
}
