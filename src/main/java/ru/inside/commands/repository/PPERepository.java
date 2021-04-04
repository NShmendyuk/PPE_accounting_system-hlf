package ru.inside.commands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inside.commands.entity.PPE;

public interface PPERepository extends JpaRepository<PPE, Long> {
}
