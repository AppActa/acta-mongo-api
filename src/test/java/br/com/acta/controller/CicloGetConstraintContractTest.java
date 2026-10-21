package br.com.acta.controller;

import br.com.acta.common.config.swagger.openapi.FormularioOpenapi;
import br.com.acta.common.config.swagger.openapi.IshikawaOpenapi;
import br.com.acta.common.config.swagger.openapi.LicaoAprendidaOpenapi;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Positive;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CicloGetConstraintContractTest {
    @Test
    void deveValidarIdCicloPositivoNosTresGetSemConflitoDeOverride() throws Exception {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            ExecutableValidator validator = factory.getValidator().forExecutables();

            assertPositiveCycleContract(
                    validator,
                    FormularioOpenapi.class,
                    FormularioController.class,
                    new Class<?>[]{Long.class, boolean.class},
                    new Object[]{1L, false}
            );
            assertPositiveCycleContract(
                    validator,
                    IshikawaOpenapi.class,
                    IshikawaController.class,
                    new Class<?>[]{Long.class},
                    new Object[]{1L}
            );
            assertPositiveCycleContract(
                    validator,
                    LicaoAprendidaOpenapi.class,
                    LicaoAprendidaController.class,
                    new Class<?>[]{Long.class},
                    new Object[]{1L}
            );
        }
    }

    private void assertPositiveCycleContract(
            ExecutableValidator validator,
            Class<?> contractType,
            Class<?> controllerType,
            Class<?>[] parameterTypes,
            Object[] validArguments
    ) throws Exception {
        Method contractMethod = contractType.getMethod("buscar", parameterTypes);
        Method implementationMethod = controllerType.getMethod("buscar", parameterTypes);
        assertTrue(contractMethod.getParameters()[0].isAnnotationPresent(Positive.class));
        assertFalse(implementationMethod.getParameters()[0].isAnnotationPresent(Positive.class));

        Object contractProbe = Proxy.newProxyInstance(contractType.getClassLoader(), new Class<?>[]{contractType}, (proxy, method, args) -> null);
        assertEquals(0, validator.validateParameters(contractProbe, contractMethod, validArguments).size());

        Object[] invalidArguments = validArguments.clone();
        invalidArguments[0] = 0L;
        assertEquals(1, validator.validateParameters(contractProbe, contractMethod, invalidArguments).size());
    }
}
