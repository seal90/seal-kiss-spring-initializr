package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.converter;

import {{MAVEN_PACKAGE_NAME}}.client.demo.req.DataReturnMultiReq;
import {{MAVEN_PACKAGE_NAME}}.client.demo.req.DataReturnReq;
import {{MAVEN_PACKAGE_NAME}}.client.demo.req.SaveDemoReq;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoDTOConverter {

    DemoDTOConverter INSTANCE = Mappers.getMapper(DemoDTOConverter.class);

    DemoDTO from(SaveDemoReq req);

    DemoDTO from(DataReturnReq req);

    DemoDTO from(DataReturnMultiReq req);

}
