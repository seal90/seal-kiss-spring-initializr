package com.github.seal90.kiss.multi.service.integration;

import io.github.seal90.kiss.base.request.PageRequest;
import io.github.seal90.kiss.base.result.Page;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import com.github.seal90.kiss.multi.service.demo.dto.DemoDTO;
import com.github.seal90.kiss.multi.service.demo.dto.DemoQueryDTO;

import java.util.List;

public interface DemoIntegration {

    Integer saveDemo(DemoDomain demoDomain);

    DemoDomain findById(Long id);

    List<DemoDomain> findMany(DemoDTO dto);

    Page<DemoDomain> findPage(PageRequest req, DemoQueryDTO dto);

    void callBipartite();
}
