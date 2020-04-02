package de.procilon.oss.yamp.api.caller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import de.procilon.oss.yamp.api.shared.Request;
import de.procilon.oss.yamp.api.shared.Response;
import de.procilon.oss.yamp.serialization.CredentialContainer;
import de.procilon.oss.yamp.serialization.Message;
import de.procilon.oss.yamp.serialization.RequestContainer;
import de.procilon.oss.yamp.serialization.ResponseContainer;
import lombok.RequiredArgsConstructor;

/**
 * @author wolffs
 * @author fichtelmannm
 */
@RequiredArgsConstructor
public class PeerAPI
{
    private final Transport                              transport;
    private final Function<Message, CredentialContainer> authenticator;
    
    public <T extends Response> CompletionStage<T> process( Request<T> request, Function<Message, T> decoder )
    {
        Message requestMessage = request.toMessage();
        RequestContainer requestContainer = new RequestContainer( requestMessage.encode(), authenticator.apply( requestMessage ) );
        
        return transport.transmit( requestContainer.encode() )
                .thenCompose( handled( ResponseContainer::decode ) )
                .thenApply( ResponseContainer::getMessage )
                .thenCompose( handled( Message::decode ) )
                .thenCompose( handled( decoder::apply ) );
    }
    
    public <T extends Response> T processSync( Request<T> request, Function<Message, T> decoder ) throws InterruptedException
    {
        try
        {
            return process( request, decoder ).toCompletableFuture().get();
        }
        catch ( ExecutionException e )
        {
            if ( e.getCause() == null )
            {
                throw new IllegalStateException( e );
            }
            if ( e.getCause() instanceof RuntimeException )
            {
                throw (RuntimeException) e.getCause();
            }
            else
            {
                throw new IllegalStateException( e.getCause() );
            }
        }
    }
    
    private static <T, R> Function<T, CompletionStage<R>> handled( Function<T, R> f )
    {
        return input -> {
            CompletableFuture<R> future = new CompletableFuture<>();
            try
            {
                future.complete( f.apply( input ) );
            }
            catch ( RuntimeException e )
            {
                future.completeExceptionally( e );
            }
            
            return future;
        };
    }
}
