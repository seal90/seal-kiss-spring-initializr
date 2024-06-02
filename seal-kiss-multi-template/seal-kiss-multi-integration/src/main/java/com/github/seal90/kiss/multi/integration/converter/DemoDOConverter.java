package com.github.seal90.kiss.multi.integration.converter;

import com.github.seal90.kiss.multi.integration.db.dos.DemoDO;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoDOConverter {

    DemoDOConverter INSTANCE = Mappers.getMapper(DemoDOConverter.class);

    @Mappings(
            @Mapping(target="durationAttr", expression = "java(demoDomain.getDurationAttr()==null? null : demoDomain.getDurationAttr().toMillis())")
    )
    DemoDO from(DemoDomain demoDomain);
}
