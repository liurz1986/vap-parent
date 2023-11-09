package com.vrv.vap.common.utils.validate;

import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author wh1107066
 * @date 2021/7/10 10:39
 */
public class ValidateCommonUtils {
    /**
     * @param t
     * @param groups
     * @param <T>
     * @return
     */
    public static <T> String getErrorResult(T t, Class<?>... groups){
        StringBuilder errorMsg = new StringBuilder();
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        Validator validator = vf.getValidator();
        Set<ConstraintViolation<T>> validate = validator.validate(t, groups);
        for (ConstraintViolation<T> tConstraintViolation : validate) {
            PathImpl  propertyPath = (PathImpl)tConstraintViolation.getPropertyPath();
            String field = propertyPath.getLeafNode().getName();
            Object invalidValue = tConstraintViolation.getInvalidValue();
            String message = tConstraintViolation.getMessage();
            errorMsg.append("field:").append(field).append(" ,");
            errorMsg.append("message:").append(message).append(",");
            errorMsg.append("invalidValue:").append(invalidValue == null ?  "空" : invalidValue).append(" ,");
        }
        return errorMsg.toString();
    }
}
