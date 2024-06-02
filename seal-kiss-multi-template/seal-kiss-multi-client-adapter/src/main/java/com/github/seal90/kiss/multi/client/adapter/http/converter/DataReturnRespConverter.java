package com.github.seal90.kiss.multi.client.adapter.http.converter;

import com.github.seal90.kiss.multi.client.demo.resp.DataReturnResp;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataReturnRespConverter {

    DataReturnRespConverter INSTANCE = Mappers.getMapper(DataReturnRespConverter.class);

    DataReturnResp from(DemoDomain demoDomain);
}
