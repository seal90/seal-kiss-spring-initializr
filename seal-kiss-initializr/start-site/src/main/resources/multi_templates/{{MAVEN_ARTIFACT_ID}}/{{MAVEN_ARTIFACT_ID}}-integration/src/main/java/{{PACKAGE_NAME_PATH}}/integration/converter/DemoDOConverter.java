package {{MAVEN_PACKAGE_NAME}}.integration.converter;

import {{MAVEN_PACKAGE_NAME}}.integration.db.dos.DemoDO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoDOConverter {

    DemoDOConverter INSTANCE = Mappers.getMapper(DemoDOConverter.class);

    @Mappings(
            @Mapping(target="durationAttr", expression = "java(demoDomain.getDurationAttr()==null? null : demoDomain.getDurationAttr().toMillis())")
    )
    DemoDO from(DemoDomain demoDomain);
}
