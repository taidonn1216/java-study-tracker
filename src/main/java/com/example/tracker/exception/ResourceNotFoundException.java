package com.example.tracker.exception;

/**
 * 対象リソースが存在しない場合にスローされる例外。
 * 
 * @author trcker-team
 * @since 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
