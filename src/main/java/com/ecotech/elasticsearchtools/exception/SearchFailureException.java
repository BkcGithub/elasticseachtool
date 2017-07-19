package com.ecotech.elasticsearchtools.exception;

public class SearchFailureException extends Exception {

    private static final long serialVersionUID = 4236270292772293617L;

    public SearchFailureException(Throwable e) {
        super(e);
    }
}
