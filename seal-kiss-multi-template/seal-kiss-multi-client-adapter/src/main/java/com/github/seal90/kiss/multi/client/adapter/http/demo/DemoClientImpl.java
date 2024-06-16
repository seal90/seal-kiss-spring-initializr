package com.github.seal90.kiss.multi.client.adapter.http.demo;

import com.github.seal90.kiss.multi.client.adapter.http.converter.*;
import com.github.seal90.kiss.multi.client.demo.req.SaveDemoReq;
import com.github.seal90.kiss.multi.client.demo.resp.*;
import com.github.seal90.kiss.multi.common.constant.MonitorLogOrigin;
import com.github.seal90.kiss.multi.common.enums.BizMetricLogScene;
import com.github.seal90.kiss.multi.common.enums.BizNotifyLogScene;
import io.github.seal90.kiss.base.result.Page;
import io.github.seal90.kiss.base.result.Result;
import com.github.seal90.kiss.multi.client.demo.DemoClient;
import com.github.seal90.kiss.multi.client.demo.req.DataReturnMultiReq;
import com.github.seal90.kiss.multi.client.demo.vo.DemoVO;
import com.github.seal90.kiss.multi.client.demo.req.DataPageReq;
import com.github.seal90.kiss.multi.client.demo.req.DataReturnReq;
import com.github.seal90.kiss.multi.service.demo.domain.DemoDomain;
import com.github.seal90.kiss.multi.service.demo.service.DemoService;
import io.github.seal90.kiss.core.log.MetricLog;
import io.github.seal90.kiss.core.log.MonitorLog;
import io.github.seal90.kiss.core.log.NotifyLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RefreshScope
public class DemoClientImpl implements DemoClient {

    @Autowired
    private DemoService demoService;

    @Value("${hello:default}")
    private String hello;

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

    @Override
    @MonitorLog(origin = MonitorLogOrigin.HTTP_IN, enableArgs = true)
    public Result<ConfigPriorityResp> configPriority() {
        ConfigPriorityResp resp = new ConfigPriorityResp();
        resp.setValue(hello);

        return Result.ok(resp);
    }


}
