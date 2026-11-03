package io.github.carolinobaulino.paymentslab;

import org.springframework.boot.SpringApplication;

public class TestPaymentsLabApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentsLabApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
