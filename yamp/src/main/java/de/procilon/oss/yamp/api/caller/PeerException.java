package de.procilon.oss.yamp.api.caller;

public class PeerException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    private String            type;
    private String            originalMessage;
    
    public PeerException( String type, String originalMessage )
    {
        super( type + ": " + originalMessage );
        this.type = type;
        this.originalMessage = originalMessage;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getOriginalMessage()
    {
        return originalMessage;
    }
}
