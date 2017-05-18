package com.homeipliance.biometrics.spo2.nonin.ipod;

public class InvalidStatusException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -6075460295708385966L;

    public InvalidStatusException()
    {
    }

    public InvalidStatusException(String message)
    {
        super(message);
    }

    public InvalidStatusException(Throwable cause)
    {
        super(cause);
    }

    public InvalidStatusException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
