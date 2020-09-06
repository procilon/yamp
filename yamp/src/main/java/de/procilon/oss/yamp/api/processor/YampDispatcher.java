package de.procilon.oss.yamp.api.processor;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;

import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.serialization.Message;
import de.procilon.oss.yamp.serialization.RequestContainer;
import de.procilon.oss.yamp.serialization.ResponseContainer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor( force = true, access = AccessLevel.PACKAGE )
@AllArgsConstructor
public class YampDispatcher
{
    private final Function<String, Optional<CredentialValidator>> validatorRegistry;
    private final Function<String, Optional<MessageProcessor>>    processorRegistry;
    private final YampDispatcherErrors                            errors;
    private final boolean                                         logErrors;
    
    public YampDispatcher( Function<String, Optional<CredentialValidator>> validatorRegistry,
            Function<String, Optional<MessageProcessor>> processorRegistry )
    {
        this( validatorRegistry, processorRegistry, YampDispatcherErrors.DEFAULT, true );
    }
    
    public ByteBuffer process( ByteBuffer input )
    {
        try
        {
            RequestContainer messageContainer = RequestContainer.decode( input );
            String credentialType = messageContainer.getCredential().getType();
            
            RequestContext context = new RequestContext();
            
            CredentialValidator validator = validatorRegistry.apply( credentialType )
                    .orElseThrow( () -> errors.unknownCredentialType( credentialType ) );
            
            if ( !validator.validate( messageContainer.getMessage().slice(), messageContainer.getCredential().getValue().slice(),
                    context ) )
            {
                throw errors.credentialValidationFailed( context );
            }
            
            Message message = Message.decode( messageContainer.getMessage() );
            MessageProcessor processor = processorRegistry.apply( message.getType() )
                    .orElseThrow( () -> errors.unknownMessageType( message.getType() ) );
            
            Message response = processor.process( message, context );
            ResponseContainer responseContainer = ResponseContainer.ofSuccess( response.encode() );
            
            return responseContainer.encode();
        }
        catch ( Throwable t )
        {
            return errors.toErrorContainer( t, logErrors );
        }
    }
}
