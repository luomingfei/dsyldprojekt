package com.mmd.exception;

/**
 * 参数校验异常
 * @author dsccc
 */
public class ValidatorException extends RuntimeException {

    public ValidatorException(String message) {
        super(message);
    }

}
