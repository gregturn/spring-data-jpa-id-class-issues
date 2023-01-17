
package com.example.demo;

import com.example.demo.EmbeddedId.CustomerWithEmbedId;
import com.example.demo.EmbeddedId.CustomerWithEmbedIdRepository;
import com.example.demo.EmbeddedId.VipCustomerWithEmbedId;
import com.example.demo.IdClass.CustomerWithIdClass;
import com.example.demo.IdClass.CustomerWithIdClassRepository;
import com.example.demo.IdClass.VipCustomerWithIdClass;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
