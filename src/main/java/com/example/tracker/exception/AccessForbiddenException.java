package com.example.tracker.exception;

/**
 * 対象リソースへアクセス権がない場合にスローされる例外。
 */
public class AccessForbiddenException extends RuntimeException {
    public AccessForbiddenException(String message) {
        super(message);
    }

}
