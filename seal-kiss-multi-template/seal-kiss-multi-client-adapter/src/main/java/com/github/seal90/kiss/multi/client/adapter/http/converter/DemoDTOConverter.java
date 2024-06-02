package com.github.seal90.kiss.multi.client.adapter.http.converter;

import com.github.seal90.kiss.multi.client.demo.req.DataReturnMultiReq;
import com.github.seal90.kiss.multi.client.demo.req.DataReturnReq;
import com.github.seal90.kiss.multi.client.demo.req.SaveDemoReq;
import com.github.seal90.kiss.multi.service.demo.dto.DemoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoDTOConverter {

    DemoDTOConverter INSTANCE = Mappers.getMapper(DemoDTOConverter.class);

    DemoDTO from(SaveDemoReq req);

    DemoDTO from(DataReturnReq req);

    DemoDTO from(DataReturnMultiReq req);

}
