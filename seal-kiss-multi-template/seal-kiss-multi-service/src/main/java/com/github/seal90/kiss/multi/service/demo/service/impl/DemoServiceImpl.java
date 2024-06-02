package com.github.seal90.kiss.multi.service.demo.service.impl;

import com.github.seal90.kiss.multi.common.enums.BizErrorReason;
import com.github.seal90.kiss.multi.service.demo.converter.DemoDomainConverter;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import com.github.seal90.kiss.multi.service.demo.dto.DemoDTO;
import com.github.seal90.kiss.multi.service.demo.dto.DemoQueryDTO;
import com.github.seal90.kiss.multi.service.demo.service.DemoService;
import com.github.seal90.kiss.multi.service.integration.DemoIntegration;
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
