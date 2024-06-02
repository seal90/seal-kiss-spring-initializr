package {{MAVEN_PACKAGE_NAME}}.service.integration;

import io.github.seal90.kiss.base.request.PageRequest;
import io.github.seal90.kiss.base.result.Page;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoDTO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoQueryDTO;

import java.util.List;

public interface DemoIntegration {

    Integer saveDemo(DemoDomain demoDomain);

    DemoDomain findById(Long id);

    List<DemoDomain> findMany(DemoDTO dto);

    Page<DemoDomain> findPage(PageRequest req, DemoQueryDTO dto);

    void callBipartite();
}
