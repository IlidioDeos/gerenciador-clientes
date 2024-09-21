package br.com.ibmec.gerenciador_clientes.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class IdadeMinimaValidator implements ConstraintValidator<IdadeMinima, LocalDate> {

    private int idadeMinima;

    @Override
    public void initialize(IdadeMinima constraintAnnotation) {
        this.idadeMinima = constraintAnnotation.valor();
    }

    @Override
    public boolean isValid(LocalDate dataNascimento, ConstraintValidatorContext context) {
        if (dataNascimento == null) {
            return false;
        }
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        return idade >= idadeMinima;
    }
}