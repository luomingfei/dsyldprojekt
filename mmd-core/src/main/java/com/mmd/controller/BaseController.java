package com.mmd.controller;

import com.mmd.TrimUtils;
import com.mmd.exception.ValidatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Slf4j
public abstract class BaseController {

    protected String getErrorMessage(BindingResult bindingResult) {
        String msg = "";
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            msg += "{" + error.getField() + "}" + error.getDefaultMessage() + ",";
        }
        msg = msg.endsWith(",") ? msg.substring(0, msg.length() - 1) : msg;
        return msg;
    }

    protected void paramsValidate(Object form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidatorException(getErrorMessage(bindingResult));
        }

        try {
            TrimUtils.beanAttributeValueTrim(form);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

}
