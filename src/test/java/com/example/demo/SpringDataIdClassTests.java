package com.example.demo;

import com.example.demo.IdClass.CustomerWithIdClassRepository;
import com.example.demo.IdClass.VipCustomerWithIdClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class SpringDataIdClassTests {

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
	CustomerWithIdClassRepository repositoryIdClassVersion;

	@Autowired
	TransactionTemplate txTemplate;

	@BeforeEach
	void setup() {

		txTemplate.executeWithoutResult(tx -> {
			repositoryIdClassVersion.deleteAll();
		});
	}

	@Test
	void idClassWithInnerTransaction() {
		doStuff();
	}

	@Test
	void idClassWithTransaction() {
		txTemplate.execute(status -> doStuff());
	}

	@Test
	@Transactional
	void idClassWithTransactional() {
		doStuff();
	}

	private VipCustomerWithIdClass doStuff() {
//        CustomerWithIdClass customer = new CustomerWithIdClass("a", "b");
//        customer.setVersionId(123L);
//        customer.setUnitId(456L);
//
//        customer = repositoryIdClassVersion.save(customer);//save object of base class, ok
//
//        customer.setFirstName("a2");
//        customer = repositoryIdClassVersion.save(customer);//modify object of base class and save again, ok

		VipCustomerWithIdClass vipCustomer = new VipCustomerWithIdClass("a", "b", "888");
		vipCustomer.setVersionId(987L);
		vipCustomer.setUnitId(654L);

		vipCustomer = repositoryIdClassVersion.save(vipCustomer);//save object of subclass, ok

		vipCustomer.setVipNumber("999");
		vipCustomer = repositoryIdClassVersion.save(vipCustomer);//modify object of subclass and save again, NOT OK
		// ↑ THIS FAILS BECAUSE OF PRIMARY KEY CONFLICT. INSERT STATEMENT WAS USED INSTEAD OF UPDATE, WHY?
		// this failure only happens when:
		// 1. base class uses IdClass for composite primary key
		// 2. saving an instance of the subclass for the second time after modification

		return vipCustomer;
	}
}
