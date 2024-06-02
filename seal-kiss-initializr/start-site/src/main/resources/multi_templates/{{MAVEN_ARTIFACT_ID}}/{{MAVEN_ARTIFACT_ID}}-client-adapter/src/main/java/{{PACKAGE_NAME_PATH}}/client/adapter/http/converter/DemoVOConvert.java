package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.converter;

import {{MAVEN_PACKAGE_NAME}}.client.demo.vo.DemoVO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DemoVOConvert {
    DemoVOConvert INSTANCE = Mappers.getMapper(DemoVOConvert.class);

    List<DemoVO> from(List<DemoDomain> demoDomains);

}
