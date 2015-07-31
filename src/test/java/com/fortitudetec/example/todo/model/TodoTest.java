package com.fortitudetec.example.todo.model;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoTest {

    private Todo _todo;
    private Validator _validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Before
    public void setUp() {
        _todo = new Todo();
    }

    @Test
    public void testItemIsRequired() {
        Set<ConstraintViolation<Todo>> violations = _validator.validateProperty(_todo, "item");
        assertThat(violations).hasSize(1);
    }

}