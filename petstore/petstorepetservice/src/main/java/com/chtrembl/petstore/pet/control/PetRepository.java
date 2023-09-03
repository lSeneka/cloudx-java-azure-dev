package com.chtrembl.petstore.pet.control;

import com.chtrembl.petstore.pet.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<PetEntity, Long> {
}
