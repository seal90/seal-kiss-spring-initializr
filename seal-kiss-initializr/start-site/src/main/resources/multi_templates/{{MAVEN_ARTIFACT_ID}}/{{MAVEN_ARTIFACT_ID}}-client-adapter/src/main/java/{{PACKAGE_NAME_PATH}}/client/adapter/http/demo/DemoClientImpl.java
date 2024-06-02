package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.demo;

import {{MAVEN_PACKAGE_NAME}}.client.adapter.http.converter.*;
import {{MAVEN_PACKAGE_NAME}}.client.demo.req.SaveDemoReq;
import {{MAVEN_PACKAGE_NAME}}.client.demo.resp.SaveDemoResp;
import {{MAVEN_PACKAGE_NAME}}.common.constant.MonitorLogOrigin;
import {{MAVEN_PACKAGE_NAME}}.common.enums.BizMetricLogScene;
import {{MAVEN_PACKAGE_NAME}}.common.enums.BizNotifyLogScene;
import io.github.seal90.kiss.base.result.Page;
import io.github.seal90.kiss.base.result.Result;
import {{MAVEN_PACKAGE_NAME}}.client.demo.DemoClient;
import {{MAVEN_PACKAGE_NAME}}.client.demo.req.DataReturnMultiReq;
import {{MAVEN_PACKAGE_NAME}}.client.demo.resp.DataReturnMultiResp;
import {{MAVEN_PACKAGE_NAME}}.client.demo.vo.DemoVO;
import {{MAVEN_PACKAGE_NAME}}.client.demo.req.DataPageReq;
import {{MAVEN_PACKAGE_NAME}}.client.demo.req.DataReturnReq;
import {{MAVEN_PACKAGE_NAME}}.client.demo.resp.DataPageResp;
import {{MAVEN_PACKAGE_NAME}}.client.demo.resp.DataReturnResp;
import {{MAVEN_PACKAGE_NAME}}.service.demo.domain.DemoDomain;
import {{MAVEN_PACKAGE_NAME}}.service.demo.service.DemoService;
import io.github.seal90.kiss.core.log.MetricLog;
import io.github.seal90.kiss.core.log.MonitorLog;
import io.github.seal90.kiss.core.log.NotifyLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
public class DemoClientImpl implements DemoClient {

    @Autowired
    private DemoService demoService;

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<Void> noDataReturn() {
        log.info("hello world");
        NotifyLog.log(BizNotifyLogScene.TEST_NOTIFY_1);
        MetricLog.log(BizMetricLogScene.TEST_METRIC_1);
        return Result.ok();
    }

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<SaveDemoResp> saveDemo(SaveDemoReq req) {

        Integer id = demoService.saveDemo(DemoDTOConverter.INSTANCE.from(req));
        SaveDemoResp resp = new SaveDemoResp();
        resp.setId(id);
        return Result.ok(resp);
    }

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<DataReturnResp> dataReturn(@Validated DataReturnReq req) {

        DemoDomain demoDomain = demoService.dataReturn(DemoDTOConverter.INSTANCE.from(req));

        return Result.ok(DataReturnRespConverter.INSTANCE.from(demoDomain));
    }

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<DataReturnMultiResp> dataReturnMulti(DataReturnMultiReq req) {

        List<DemoDomain> demoDomains = demoService.dataReturnMulti(DemoDTOConverter.INSTANCE.from(req));
        List<DemoVO> demoVOS = DemoVOConvert.INSTANCE.from(demoDomains);
        return Result.ok(DataReturnMultiResp.builder().demoDTOs(demoVOS).build());
    }
    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<DataPageResp<DemoVO>> dataPage(@Validated DataPageReq req) {
        Page<DemoDomain> page = demoService.dataPage(req, DemoQueryDTOConvert.INSTANCE.from(req));
        return Result.ok(DataPageRespConvert.INSTANCE.from(page));

    }

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<Void> throwServiceException() {
        demoService.throwServiceException();
        return Result.ok();
    }

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<Void> throwRuntimeException() {
        demoService.throwRuntimeException();
        return Result.ok();
    }

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<Void> callBipartite() {
        demoService.callBipartite();
        return Result.ok();
    }
}
