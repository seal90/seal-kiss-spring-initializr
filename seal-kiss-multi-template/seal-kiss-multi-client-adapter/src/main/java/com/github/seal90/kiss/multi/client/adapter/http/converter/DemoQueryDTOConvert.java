package com.github.seal90.kiss.multi.client.adapter.http.converter;

import com.github.seal90.kiss.multi.client.demo.req.DataPageReq;
import com.github.seal90.kiss.multi.service.demo.dto.DemoQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoQueryDTOConvert {

    DemoQueryDTOConvert INSTANCE = Mappers.getMapper(DemoQueryDTOConvert.class);

    DemoQueryDTO from(DataPageReq req);
}
