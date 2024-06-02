package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.converter;

import {{MAVEN_PACKAGE_NAME}}.client.demo.resp.DataReturnResp;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataReturnRespConverter {

    DataReturnRespConverter INSTANCE = Mappers.getMapper(DataReturnRespConverter.class);

    DataReturnResp from(DemoDomain demoDomain);
}
