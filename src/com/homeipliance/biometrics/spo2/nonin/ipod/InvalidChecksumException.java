package com.homeipliance.biometrics.spo2.nonin.ipod;

public class InvalidChecksumException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 3239703736017841917L;

    public InvalidChecksumException()
    {
    }

    public InvalidChecksumException(String message)
    {
        super(message);
    }

    public InvalidChecksumException(Throwable cause)
    {
        super(cause);
    }

    public InvalidChecksumException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
