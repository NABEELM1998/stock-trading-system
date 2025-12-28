package com.nabeel.order_service.exceptions;

public class OrderExecutionException extends RuntimeException{
    public OrderExecutionException(String message){
        super(message);
    }
}
