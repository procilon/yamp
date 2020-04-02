package de.procilon.oss.yamp;

import lombok.Getter;

@Getter
public class RelayedException extends YampException
{
    private static final long serialVersionUID = 1L;
    
    private String            type;
    private String            originalMessage;
    
    public RelayedException( String type, String originalMessage )
    {
        super( type + ": " + originalMessage );
        this.type = type;
        this.originalMessage = originalMessage;
    }
}