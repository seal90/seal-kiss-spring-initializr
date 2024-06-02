package com.github.seal90.kiss.multi.client.adapter.http.converter;

import com.github.seal90.kiss.multi.client.demo.vo.DemoVO;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DemoVOConvert {
    DemoVOConvert INSTANCE = Mappers.getMapper(DemoVOConvert.class);

    List<DemoVO> from(List<DemoDomain> demoDomains);

}
