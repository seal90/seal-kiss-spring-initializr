package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.converter;

import {{MAVEN_PACKAGE_NAME}}.client.demo.req.DataPageReq;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DemoQueryDTOConvert {

    DemoQueryDTOConvert INSTANCE = Mappers.getMapper(DemoQueryDTOConvert.class);

    DemoQueryDTO from(DataPageReq req);
}
