package de.procilon.oss.yamp.api.processor;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.serialization.ResponseContainer;

public interface YampDispatcherErrors
{
    YampDispatcherErrors DEFAULT = new YampDispatcherErrors()
    {};
    
    default RuntimeException unknownCredentialType( String credentialType )
    {
        return new IllegalArgumentException( "unknown credential type: " + credentialType );
    }
    
    default RuntimeException credentialValidationFailed( RequestContext context )
    {
        return new IllegalStateException( "credential validation failed" );
    }
    
    default RuntimeException unknownMessageType( String messageType )
    {
        return new IllegalArgumentException( "unknown message type: " + messageType );
    }
    
    default ByteBuffer toErrorContainer( Throwable t, boolean logErrors )
    {
        if ( logErrors )
        {
            Logger log = Logger.getLogger( YampDispatcher.class.getName() );
            log.log( Level.SEVERE, t.getMessage(), t );
        }
        return ResponseContainer.ofError( t ).encode();
    }
}
