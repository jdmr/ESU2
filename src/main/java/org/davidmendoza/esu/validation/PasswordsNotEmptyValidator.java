/*
 * The MIT License
 *
 * Copyright 2015 J. David Mendoza <jdmendoza@swau.edu>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.davidmendoza.esu.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;

/**
 * @author Petri Kainulainen
 */
public class PasswordsNotEmptyValidator implements ConstraintValidator<PasswordsNotEmpty, Object> {

    private String validationTriggerFieldName;
    private String passwordFieldName;
    private String passwordVerificationFieldName;

    @Override
    public void initialize(PasswordsNotEmpty constraintAnnotation) {
        validationTriggerFieldName = constraintAnnotation.triggerFieldName();
        passwordFieldName = constraintAnnotation.passwordFieldName();
        passwordVerificationFieldName = constraintAnnotation.passwordVerificationFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        try {
            Object validationTrigger = ValidatorUtil.getFieldValue(value, validationTriggerFieldName);
            if (validationTrigger == null) {
                return passwordFieldsAreValid(value, context);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("Exception occurred during validation", ex);
        }

        return true;
    }

    private boolean passwordFieldsAreValid(Object value, ConstraintValidatorContext context) throws NoSuchFieldException, IllegalAccessException {
        boolean passwordWordFieldsAreValid = true;

        String password = (String) ValidatorUtil.getFieldValue(value, passwordFieldName);
        if (isNullOrEmpty(password)) {
            ValidatorUtil.addValidationError(passwordFieldName, context);
            passwordWordFieldsAreValid = false;
        }

        String passwordVerification = (String) ValidatorUtil.getFieldValue(value, passwordVerificationFieldName);
        if (isNullOrEmpty(passwordVerification)) {
            ValidatorUtil.addValidationError(passwordVerificationFieldName, context);
            passwordWordFieldsAreValid = false;
        }

        return passwordWordFieldsAreValid;
    }

    private boolean isNullOrEmpty(String field) {
        return StringUtils.isEmpty(field);
    }
}
