package com.github.seal90.kiss.multi.client.adapter.http.converter;

import io.github.seal90.kiss.base.result.Page;
import com.github.seal90.kiss.multi.client.demo.resp.DataPageResp;
import com.github.seal90.kiss.multi.client.demo.vo.DemoVO;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataPageRespConvert {
    DataPageRespConvert INSTANCE = Mappers.getMapper(DataPageRespConvert.class);

    DataPageResp<DemoVO> from(Page<DemoDomain> page);
}
