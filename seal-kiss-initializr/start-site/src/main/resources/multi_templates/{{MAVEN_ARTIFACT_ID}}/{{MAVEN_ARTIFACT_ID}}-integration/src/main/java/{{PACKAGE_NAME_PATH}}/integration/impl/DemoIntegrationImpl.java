package {{MAVEN_PACKAGE_NAME}}.integration.impl;

import com.github.pagehelper.PageHelper;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.HelloWorldClient;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.req.SayHelloRequest;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.resp.SayHelloResp;
import {{MAVEN_PACKAGE_NAME}}.integration.converter.DemoDOConverter;
import {{MAVEN_PACKAGE_NAME}}.integration.db.dao.DemoDAO;
import {{MAVEN_PACKAGE_NAME}}.integration.db.dos.DemoDO;
import {{MAVEN_PACKAGE_NAME}}.integration.db.dos.DemoParam;
import {{MAVEN_PACKAGE_NAME}}.integration.feign.DemoFeign;
import io.github.seal90.kiss.base.request.PageRequest;
import io.github.seal90.kiss.base.result.Page;
import {{MAVEN_PACKAGE_NAME}}.integration.converter.DemoDomainConverter;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoDTO;
import {{MAVEN_PACKAGE_NAME}}.service.demo.dto.DemoQueryDTO;
import {{MAVEN_PACKAGE_NAME}}.service.integration.DemoIntegration;
import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.core.log.LazyToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
public class DemoIntegrationImpl implements DemoIntegration {

    @Autowired
    private DemoDAO demoDAO;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private DemoFeign demoFeign;

    @Autowired
    private LazyToString lazyToString;

    @Autowired
    private HelloWorldClient helloWorldClientFacade;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Integer saveDemo(DemoDomain demoDomain) {
        DemoDO demoDO = DemoDOConverter.INSTANCE.from(demoDomain);
        return demoDAO.insert(demoDO);
    }

    @Override
    @Transactional
    public DemoDomain findById(Long id) {
        DemoDO demoDO = demoDAO.selectByPrimaryKey(id);
        return DemoDomainConverter.INSTANCE.from(demoDO);
    }

    @Override
    public List<DemoDomain> findMany(DemoDTO dto) {
        DemoParam param = new DemoParam();
        DemoParam.Criteria criteria = param.createCriteria();
        criteria.andStringAttrEqualTo(dto.getStringAttr());

        List<DemoDO> demoDOS = demoDAO.selectByParam(param);
        return DemoDomainConverter.INSTANCE.from(demoDOS);
    }

    @Override
    public Page<DemoDomain> findPage(PageRequest req, DemoQueryDTO dto) {

        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        DemoParam param = new DemoParam();
        DemoParam.Criteria criteria = param.createCriteria();
        List<String> stringAttrs = dto.getStringAttrs();
        if(!CollectionUtils.isEmpty(stringAttrs)) {
            criteria.andStringAttrIn(stringAttrs);
        }

        List<DemoDO> demoDOS = demoDAO.selectByParam(param);
        com.github.pagehelper.Page<DemoDO> returnPage = (com.github.pagehelper.Page<DemoDO>)demoDOS;

        // TODO static util
        Page<DemoDomain> page = new Page<>();
        page.setPageNum(returnPage.getPageNum());
        page.setPageSize(returnPage.getPageSize());
        page.setTotalNum(returnPage.getTotal());
        page.setData(DemoDomainConverter.INSTANCE.from(demoDOS));

        return page;
    }

    @Override
    public void callBipartite() {

        SayHelloRequest request1 = new SayHelloRequest();
        request1.setName("bipartite demoFeign");
        Result<SayHelloResp> val = demoFeign.sayHello(request1);
        log.info("demoFeign: {}", lazyToString.obj(val));

        SayHelloRequest request = new SayHelloRequest();
        request.setName("bipartite helloWorldClientFacade");
        Result<SayHelloResp> result = helloWorldClientFacade.sayHello(request);
        log.info("helloWorldClientFacade: {}", lazyToString.obj(result));
    }
}
