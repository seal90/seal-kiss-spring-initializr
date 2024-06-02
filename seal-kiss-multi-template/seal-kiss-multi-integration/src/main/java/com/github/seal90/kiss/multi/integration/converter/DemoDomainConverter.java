package com.github.seal90.kiss.multi.integration.converter;

import com.github.seal90.kiss.multi.integration.db.dos.DemoDO;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DemoDomainConverter {

    DemoDomainConverter INSTANCE = Mappers.getMapper(DemoDomainConverter.class);

    @Mappings(
            @Mapping(target="durationAttr", expression = "java(demoDO.getDurationAttr()==null? null : java.time.Duration.ofMillis(demoDO.getDurationAttr()))")
    )
    DemoDomain from(DemoDO demoDO);

    List<DemoDomain> from(List<DemoDO> demoDOS);
}
