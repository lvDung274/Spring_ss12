package com.example.btthsession12.models.repositories;

import com.example.btthsession12.models.entities.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findAll();

    List<Supply> findAllByNameContainingIgnoreCase(String name);

    Optional<Supply> findByIdAndIsDeletedFalse(Long id);

    List<Supply> findByIsDeletedFalse();
}
