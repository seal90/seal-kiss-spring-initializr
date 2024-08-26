package {{MAVEN_PACKAGE_NAME}}.integration.config;

import {{MAVEN_PACKAGE_NAME}}.client.helloWorld.HelloWorldClient;
import io.github.seal90.kiss.spring.cloud.extension.feign.FeignConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @FeignConsumer(name = "seal-kiss-multi")
    private HelloWorldClient helloWorldClientFacade;

    @Value("${spring.application.name}")
    private String name;

//    @FeignConsumer(name = "seal-kiss-multi", url = "http://localhost:8080")
//    private HelloWorldClient helloWorldClientFacade;




//    @Bean
//    public DemoClient demoFacadeClient(Decoder decoder, Encoder encoder, Client client, Contract contract){
//        return Feign.builder().client(client)
//                .encoder(encoder)
//                .decoder(decoder)
//                .contract(contract)
//                .target(DemoClient.class, "http://localhost:8080");
//    }

//    @Bean
//    public DemoClient demoFacadeClient(Client client){
//        return Feign.builder().client(client)
//                .target(DemoClient.class, "http://localhost:8080");
//    }


}
