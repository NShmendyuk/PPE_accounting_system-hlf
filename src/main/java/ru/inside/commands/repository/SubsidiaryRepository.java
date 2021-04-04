package ru.inside.commands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inside.commands.entity.Subsidiary;

public interface SubsidiaryRepository extends JpaRepository<Subsidiary, Long> {
}
