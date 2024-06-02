package {{MAVEN_PACKAGE_NAME}}.client.helloWorld.req;

import lombok.Data;

/**
 * say hello 请求
 */
@Data
public class SayHelloRequest {

    private String name;
}
