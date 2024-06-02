package com.github.seal90.kiss.multi.service.demo.converter;

import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import com.github.seal90.kiss.multi.service.demo.dto.DemoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoDomainConverter {

    DemoDomainConverter INSTANCE = Mappers.getMapper(DemoDomainConverter.class);

    DemoDomain from(DemoDTO demoDTO);
}
