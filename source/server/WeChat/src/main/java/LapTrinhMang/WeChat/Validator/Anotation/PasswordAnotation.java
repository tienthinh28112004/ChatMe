package LapTrinhMang.WeChat.Validator.Anotation;

import LapTrinhMang.WeChat.Validator.CustomPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.List;

public class PasswordAnotation implements ConstraintValidator<CustomPassword,String> {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 32;
    private boolean detailedMessage;
    @Override
    public void initialize(CustomPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        detailedMessage = constraintAnnotation.detailedMessage();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if(password ==null) {return true;}
        List<Rule> rules = Arrays.asList(
                new LengthRule(6, 32),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),//phải có 1 kí tự viết hoa
                new CharacterRule(EnglishCharacterData.LowerCase, 1),//1 kí tự viết thường
                new CharacterRule(EnglishCharacterData.Digit, 1),//có 1 số
                new CharacterRule(EnglishCharacterData.Special, 1),//có 1 kí tự đặc biệt
                new WhitespaceRule()//không có khoảng trăng
        );
        //cái passwordValidator này là phương thức của passy khác với passwordValidator bên trên
        PasswordValidator passwordValidator= new PasswordValidator(rules);

        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if(result.isValid()) return true;
        return false;
    }
}
