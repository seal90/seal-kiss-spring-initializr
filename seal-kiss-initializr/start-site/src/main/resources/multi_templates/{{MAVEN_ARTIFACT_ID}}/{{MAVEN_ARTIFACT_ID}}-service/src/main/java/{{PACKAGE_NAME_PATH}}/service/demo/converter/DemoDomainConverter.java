package {{MAVEN_PACKAGE_NAME}}.service.demo.converter;

import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoDomainConverter {

    DemoDomainConverter INSTANCE = Mappers.getMapper(DemoDomainConverter.class);

    DemoDomain from(DemoDTO demoDTO);
}
