package {{MAVEN_PACKAGE_NAME}}.service.demo.service;

import io.github.seal90.kiss.base.request.PageRequest;
import io.github.seal90.kiss.base.result.Page;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoDTO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoQueryDTO;

import java.util.List;

public interface DemoService {

    Integer saveDemo(DemoDTO demoDTO);

    DemoDomain dataReturn(DemoDTO demoDTO);

    List<DemoDomain> dataReturnMulti(DemoDTO demoDTO);

    Page<DemoDomain> dataPage(PageRequest req, DemoQueryDTO demoQueryDTO);

    void throwServiceException();

    void throwRuntimeException();

    void callBipartite();
}
