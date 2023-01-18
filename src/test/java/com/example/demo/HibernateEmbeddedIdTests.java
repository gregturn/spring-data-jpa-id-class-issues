package com.example.demo;

import com.example.demo.EmbeddedId.CustomerWithEmbedId;
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

import javax.persistence.EntityManager;

@SpringBootTest
@Testcontainers
class HibernateEmbeddedIdTests {

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
    EntityManager entityManager;

    @Test
    void embeddedIdWithoutTransactional() {
        CustomerWithEmbedId customer = new CustomerWithEmbedId("a", "b");
        customer.setVersionId(123L);
        customer.setUnitId(456L);

        customer = entityManager.merge(customer);  //save object of base class, ok

        customer.setFirstName("a2");
        customer = entityManager.merge(customer);//modify object of base class and save again, ok

        VipCustomerWithEmbedId vipCustomer = new VipCustomerWithEmbedId("a", "b", "888");
        vipCustomer.setVersionId(987L);
        vipCustomer.setUnitId(654L);

        vipCustomer = entityManager.merge(vipCustomer); //save object of subclass, ok

        vipCustomer.setVipNumber("999");
        vipCustomer = entityManager.merge(vipCustomer);//modify object of subclass and save again, ok
        // using embedded id annotation, all 4 times of saving to db ok, for both pg and mysql
    }

    @Test
    @Transactional
    void embeddedIdWithTransactional() {
        CustomerWithEmbedId customer = new CustomerWithEmbedId("a", "b");
        customer.setVersionId(123L);
        customer.setUnitId(456L);

        customer = entityManager.merge(customer);  //save object of base class, ok

        customer.setFirstName("a2");
        customer = entityManager.merge(customer);//modify object of base class and save again, ok

        VipCustomerWithEmbedId vipCustomer = new VipCustomerWithEmbedId("a", "b", "888");
        vipCustomer.setVersionId(987L);
        vipCustomer.setUnitId(654L);

        vipCustomer = entityManager.merge(vipCustomer); //save object of subclass, ok

        vipCustomer.setVipNumber("999");
        vipCustomer = entityManager.merge(vipCustomer);//modify object of subclass and save again, ok
        // using embedded id annotation, all 4 times of saving to db ok, for both pg and mysql
    }
}
