package {{MAVEN_PACKAGE_NAME}}.client.demo.resp;

import {{MAVEN_PACKAGE_NAME}}.client.demo.vo.DemoVO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataReturnMultiResp {

    private List<DemoVO> demoDTOs;

}
