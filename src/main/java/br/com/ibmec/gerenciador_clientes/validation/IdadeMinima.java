package br.com.ibmec.gerenciador_clientes.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdadeMinimaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IdadeMinima {
    int valor() default 18;
    String message() default "O cliente deve ter no mínimo {valor} anos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
