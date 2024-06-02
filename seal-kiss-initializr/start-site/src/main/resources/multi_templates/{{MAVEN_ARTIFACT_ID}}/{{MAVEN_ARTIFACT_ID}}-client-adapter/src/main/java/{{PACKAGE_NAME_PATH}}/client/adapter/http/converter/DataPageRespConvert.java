package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.converter;

import io.github.seal90.kiss.base.result.Page;
import {{MAVEN_PACKAGE_NAME}}.client.demo.resp.DataPageResp;
import {{MAVEN_PACKAGE_NAME}}.client.demo.vo.DemoVO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataPageRespConvert {
    DataPageRespConvert INSTANCE = Mappers.getMapper(DataPageRespConvert.class);

    DataPageResp<DemoVO> from(Page<DemoDomain> page);
}
