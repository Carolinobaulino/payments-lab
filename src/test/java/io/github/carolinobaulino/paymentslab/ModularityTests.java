package io.github.carolinobaulino.paymentslab;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(PaymentsLabApplication.class);

    @Test
    void verifiesModuleBoundaries() {
        modules.forEach(System.out::println);
        modules.verify();
    }
}