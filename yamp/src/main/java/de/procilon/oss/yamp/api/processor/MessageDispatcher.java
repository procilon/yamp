package de.procilon.oss.yamp.api.processor;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.serialization.ErrorMessage;
import de.procilon.oss.yamp.serialization.Message;
import de.procilon.oss.yamp.serialization.RequestContainer;
import de.procilon.oss.yamp.serialization.ResponseContainer;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Log
@AllArgsConstructor
public class MessageDispatcher
{
    private final Function<String, Optional<CredentialValidator>> validatorRegistry;
    private final Function<String, Optional<MessageProcessor>>    processorRegistry;
    
    public ByteBuffer process( ByteBuffer input )
    {
        try
        {
            var messageContainer = RequestContainer.decode( input );
            var credentialType = messageContainer.getCredential().getType();
            
            var context = new RequestContext();
            
            var validator = validatorRegistry.apply( credentialType )
                    .orElseThrow( () -> new IllegalArgumentException( "unknown credential type: " + credentialType ) );
            
            if ( !validator.validate( messageContainer.getMessage().slice(), messageContainer.getCredential().getValue().slice(),
                    context ) )
            {
                throw new IllegalStateException( "credential validation failed" );
            }
            
            var message = Message.decode( messageContainer.getMessage() );
            var processor = processorRegistry.apply( message.getType() )
                    .orElseThrow( () -> new IllegalArgumentException( "unknown message type: " + message.getType() ) );
            
            var response = processor.process( message, context );
            var responseContainer = new ResponseContainer( false, response.encode() );
            
            return responseContainer.encode();
        }
        catch ( Throwable t )
        {
            log.log( Level.SEVERE, t.getMessage(), t );
            return new ResponseContainer( true, ErrorMessage.fromThrowable( t ).encode() ).encode();
        }
    }
}
