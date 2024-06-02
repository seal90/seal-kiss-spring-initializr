package {{MAVEN_PACKAGE_NAME}}.service.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class DemoQueryDTO {

    private List<String> stringAttrs;
}
