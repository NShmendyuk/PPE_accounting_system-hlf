package ru.inside.commands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inside.commands.entity.PPE;

import java.util.Optional;

public interface PPERepository extends JpaRepository<PPE, Long> {
    Optional<PPE> findByInventoryNumber(String inventoryNumber);
    void deleteByInventoryNumber(String inventoryNumber);
    boolean existsByInventoryNumber(String inventoryNumber);
}
