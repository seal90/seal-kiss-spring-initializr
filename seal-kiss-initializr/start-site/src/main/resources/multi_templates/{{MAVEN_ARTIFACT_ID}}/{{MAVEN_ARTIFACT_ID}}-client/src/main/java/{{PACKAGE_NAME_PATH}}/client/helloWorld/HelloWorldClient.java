package {{MAVEN_PACKAGE_NAME}}.client.helloWorld;

import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.req.SayHelloRequest;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.resp.SayHelloResp;
import io.github.seal90.kiss.base.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 演示类
 */
@RequestMapping("/helloWorld")
public interface HelloWorldClient {

    /**
     * sayHello 简单演示
     * @param request
     * @return
     */
    @PostMapping("/sayHello")
    Result<SayHelloResp> sayHello(@RequestBody SayHelloRequest request);


}
