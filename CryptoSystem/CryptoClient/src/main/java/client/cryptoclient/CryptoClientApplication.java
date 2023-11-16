package client.cryptoclient;

import client.cryptoclient.controllers.ClientCryptoProgress;
import client.cryptoclient.cryptoAlgorithms.Crypto;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.unit.DataSize;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
@SpringBootApplication
public class CryptoClientApplication {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(30));
        factory.setMaxRequestSize(DataSize.ofMegabytes(30));
        return factory.createMultipartConfig();
    }

//    @Bean
//    @Scope("singleton")
//    public ConcurrentMap<Long, ConcurrentMap<Long, ClientCryptoProgress>[]> sessionIds() {
//      return new ConcurrentHashMap<Long, ConcurrentMap<Long, ClientCryptoProgress>[]>();
//    }

    public static void main(String[] args) {
        SpringApplication.run(CryptoClientApplication.class, args);
    }

}
