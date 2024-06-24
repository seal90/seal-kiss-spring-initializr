package {{MAVEN_PACKAGE_NAME}}.integration.feign;

import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.req.SayHelloRequest;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.resp.SayHelloResp;
import io.github.seal90.kiss.base.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "seal-kiss-multi", path = "/helloWorld")
//@FeignClient(name = "seal-kiss-multi", url = "http://localhost:8080", path = "/helloWorld")
// 下面这个注解是不允许被使用的，使用 FeignClient 的path 代替
//@RequestMapping("/helloWorld")
public interface DemoFeign {

    @PostMapping("/sayHello")
    Result<SayHelloResp> sayHello(@RequestBody SayHelloRequest request);
}