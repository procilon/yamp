package de.procilon.oss.yamp;

import lombok.Getter;

/**
 * A {@link YampException} that represents an exception that occurred in the processor
 * 
 * @author fichtelmannm
 *
 */
@Getter
public class RelayedException extends YampException
{
    private static final long serialVersionUID = 1L;
    
    private String            type;
    private String            originalMessage;
    
    /**
     * Create a new {@link RelayedException} with the specified original cause and type
     * 
     * @param type
     *            the type of the original exception, typically the class name
     * @param originalMessage
     *            the message of the original exception
     */
    public RelayedException( String type, String originalMessage )
    {
        super( type + ": " + originalMessage );
        this.type = type;
        this.originalMessage = originalMessage;
    }
}