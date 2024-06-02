package com.github.seal90.kiss.multi.client.demo;

import com.github.seal90.kiss.multi.client.demo.req.DataPageReq;
import com.github.seal90.kiss.multi.client.demo.req.DataReturnMultiReq;
import com.github.seal90.kiss.multi.client.demo.req.DataReturnReq;
import com.github.seal90.kiss.multi.client.demo.req.SaveDemoReq;
import com.github.seal90.kiss.multi.client.demo.resp.DataPageResp;
import com.github.seal90.kiss.multi.client.demo.resp.DataReturnMultiResp;
import com.github.seal90.kiss.multi.client.demo.resp.DataReturnResp;
import com.github.seal90.kiss.multi.client.demo.resp.SaveDemoResp;
import com.github.seal90.kiss.multi.client.demo.vo.DemoVO;
import io.github.seal90.kiss.base.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@RequestMapping("/demo")
public interface DemoClient {

    @PostMapping("/noDataReturn")
    Result<Void> noDataReturn();

    @PostMapping("/save")
    Result<SaveDemoResp> saveDemo(@RequestBody SaveDemoReq req);

    @PostMapping("/dataReturn")
    Result<DataReturnResp> dataReturn(@RequestBody DataReturnReq req);

    @PostMapping("/dataReturnMulti")
    Result<DataReturnMultiResp> dataReturnMulti(@RequestBody DataReturnMultiReq req);

    @PostMapping("/dataPage")
    Result<DataPageResp<DemoVO>> dataPage(@RequestBody DataPageReq req);

    @PostMapping("/throwServiceException")
    Result<Void> throwServiceException();

    @PostMapping("/throwRuntimeException")
    Result<Void> throwRuntimeException();

    @PostMapping("/callBipartite")
    Result<Void> callBipartite();
}
