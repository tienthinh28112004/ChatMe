package LapTrinhMang.WeChat.Validator;

import LapTrinhMang.WeChat.Validator.Anotation.PasswordAnotation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {PasswordAnotation.class})
public @interface CustomPassword {
    String message() default "Password must contain 1 uppercase letter, 1 lowercase letter, 1 special character and no spaces";

    boolean detailedMessage() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
