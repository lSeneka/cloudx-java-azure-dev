package com.chtrembl.petstore.pet.control.mapping;

import com.chtrembl.petstore.pet.entity.PetEntity;
import com.chtrembl.petstore.pet.model.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface PetMapper {
    PetMapper INSTANCE = Mappers.getMapper(PetMapper.class);

    Pet toPetModel(PetEntity source);

    default Pet.StatusEnum mapStatus(String status) {
        return Pet.StatusEnum.fromValue(status);
    }
}
