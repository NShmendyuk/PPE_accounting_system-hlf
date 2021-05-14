package ru.inside.commands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inside.commands.entity.Subsidiary;

import java.util.Optional;

public interface SubsidiaryRepository extends JpaRepository<Subsidiary, Long> {
    Optional<Subsidiary> findByName(String subsidiaryName);
}
