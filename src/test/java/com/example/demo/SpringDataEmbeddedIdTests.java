package com.example.demo;

import com.example.demo.EmbeddedId.CustomerWithEmbedId;
import com.example.demo.EmbeddedId.CustomerWithEmbedIdRepository;
import com.example.demo.EmbeddedId.VipCustomerWithEmbedId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class SpringDataEmbeddedIdTests {

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:9.6.12")
        .withDatabaseName("demo")
        .withUsername("postgres")
        .withPassword("password");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    CustomerWithEmbedIdRepository repositoryEmbedIdVersion;

    @Test
    @Transactional
    void embeddedIdWithTransactional() {
        CustomerWithEmbedId customer = new CustomerWithEmbedId("a", "b");
        customer.setVersionId(123L);
        customer.setUnitId(456L);

        repositoryEmbedIdVersion.save(customer); //save object of base class, ok

        customer.setFirstName("a2");
        repositoryEmbedIdVersion.save(customer);//modify object of base class and save again, ok

        VipCustomerWithEmbedId vipCustomer = new VipCustomerWithEmbedId("a", "b", "888");
        vipCustomer.setVersionId(987L);
        vipCustomer.setUnitId(654L);

        repositoryEmbedIdVersion.save(vipCustomer); //save object of subclass, ok

        vipCustomer.setVipNumber("999");
        repositoryEmbedIdVersion.save(vipCustomer);//modify object of subclass and save again, ok
        // using embedded id annotation, all 4 times of saving to db ok, for both pg and mysql
    }

    @Test
    void embeddedIdWithoutTransactional() {
        CustomerWithEmbedId customer = new CustomerWithEmbedId("a", "b");
        customer.setVersionId(123L);
        customer.setUnitId(456L);

        repositoryEmbedIdVersion.save(customer); //save object of base class, ok

        customer.setFirstName("a2");
        repositoryEmbedIdVersion.save(customer);//modify object of base class and save again, ok

        VipCustomerWithEmbedId vipCustomer = new VipCustomerWithEmbedId("a", "b", "888");
        vipCustomer.setVersionId(987L);
        vipCustomer.setUnitId(654L);

        repositoryEmbedIdVersion.save(vipCustomer); //save object of subclass, ok

        vipCustomer.setVipNumber("999");
        repositoryEmbedIdVersion.save(vipCustomer);//modify object of subclass and save again, ok
        // using embedded id annotation, all 4 times of saving to db ok, for both pg and mysql
    }
}
