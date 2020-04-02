package de.procilon.oss.yamp;

/**
 * Generic {@link RuntimeException} for errors originated from the yamp library.
 * 
 * @author fichtelmannm
 *
 */
public class YampException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs an {@link YampException} without an error detail message.
     */
    public YampException()
    {
        super();
    }
    
    /**
     * Constructs an {@link YampException} with the specified error detail message and cause. Note that the detail message associated with
     * cause is not automatically incorporated into this exception's detail message.
     * 
     * @param message
     *            The detail message.
     * @param cause
     *            The cause.
     */
    public YampException( String message, Throwable cause )
    {
        super( message, cause );
    }
    
    /**
     * Constructs an {@link YampException} with the specified error detail message.
     * 
     * @param message
     *            The detail message.
     */
    public YampException( String message )
    {
        super( message );
    }
    
    /**
     * Constructs an {@link YampException} with the specified cause and a detail message of (cause==null ? null : cause.toString()) (which
     * typically contains the class and detail message of cause).
     * 
     * @param cause
     *            The cause.
     */
    public YampException( Throwable cause )
    {
        super( cause );
    }
}