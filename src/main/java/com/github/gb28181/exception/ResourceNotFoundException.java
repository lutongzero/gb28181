package com.github.gb28181.exception;

public class ResourceNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;  
 
 
    public ResourceNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }


    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
