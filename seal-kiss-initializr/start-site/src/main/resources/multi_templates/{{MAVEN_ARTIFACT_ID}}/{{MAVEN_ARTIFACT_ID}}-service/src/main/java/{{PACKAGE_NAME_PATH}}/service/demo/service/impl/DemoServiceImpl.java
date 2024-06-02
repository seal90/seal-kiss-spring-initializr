package {{MAVEN_PACKAGE_NAME}}.service.demo.service.impl;

import {{MAVEN_PACKAGE_NAME}}.common.enums.BizErrorReason;
import {{MAVEN_PACKAGE_NAME}}.service.demo.converter.DemoDomainConverter;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoDTO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoQueryDTO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.service.DemoService;
import {{MAVEN_PACKAGE_NAME}}.service.integration.DemoIntegration;
import io.github.seal90.kiss.base.request.PageRequest;
import io.github.seal90.kiss.base.result.Page;
import io.github.seal90.kiss.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoIntegration demoIntegration;

    @Override
    public Integer saveDemo(DemoDTO demoDTO) {
        return demoIntegration.saveDemo(DemoDomainConverter.INSTANCE.from(demoDTO));
    }

    @Override
    public DemoDomain dataReturn(DemoDTO demoDTO) {
        return demoIntegration.findById(demoDTO.getId());
    }

    @Override
    public List<DemoDomain> dataReturnMulti(DemoDTO demoDTO) {
        return demoIntegration.findMany(demoDTO);
    }

    @Override
    public Page<DemoDomain> dataPage(PageRequest req, DemoQueryDTO demoQueryDTO) {
        return demoIntegration.findPage(req, demoQueryDTO);
    }

    @Override
    public void throwServiceException() {
        throw new ServiceException(BizErrorReason.TEST);
    }

    @Override
    public void throwRuntimeException() {
        throw new RuntimeException("测试 runtimeexception");
    }

    @Override
    public void callBipartite() {
        demoIntegration.callBipartite();
    }
}
