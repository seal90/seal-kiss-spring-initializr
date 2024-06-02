package {{MAVEN_PACKAGE_NAME}}.client.adapter.http.helloWorld;

import io.github.seal90.kiss.base.result.Result;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.HelloWorldClient;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.req.SayHelloRequest;
import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.resp.SayHelloResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloWorldClientImpl implements HelloWorldClient {

    @Override
    public Result<SayHelloResp> sayHello(SayHelloRequest request) {
        log.info("sayHello called");
        SayHelloResp resp = new SayHelloResp();
        resp.setContent("hello " + request.getName());
        return Result.ok(resp);
    }

}
