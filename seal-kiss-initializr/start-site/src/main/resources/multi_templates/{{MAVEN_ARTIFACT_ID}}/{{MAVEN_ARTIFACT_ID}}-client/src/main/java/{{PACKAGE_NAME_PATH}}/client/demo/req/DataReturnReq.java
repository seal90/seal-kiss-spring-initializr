package {{MAVEN_PACKAGE_NAME}}.client.demo.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataReturnReq {

    @NotNull
    @Min(0)
    private Long id;

}
