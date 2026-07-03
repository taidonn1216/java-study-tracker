package com.example.tracker.exception;

/**
 * 対象リソースへアクセス権がない場合にスローされる例外。
 * 
 * @author tracker-team
 * @since 1.0
 */
public class AccessForbiddenException extends RuntimeException {
    public AccessForbiddenException(String message) {
        super(message);
    }

}
