package de.procilon.oss.yamp.api.caller;

import static java.util.Objects.nonNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import de.procilon.oss.yamp.YampException;
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
public class Yamp
{
    private final YampTransport                          transport;
    private final Function<Message, CredentialContainer> authenticator;
    private final YampOptions                            options;
    
    public Yamp( YampTransport transport, Function<Message, CredentialContainer> authenticator )
    {
        this( transport, authenticator, YampOptions.EMPTY );
    }
    
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
            CompletableFuture<T> task = process( request, decoder ).toCompletableFuture();
            Duration timeout = options.getTimeout();
            if ( nonNull( timeout ) )
            {
                return task.get( timeout.toMillis(), TimeUnit.MILLISECONDS );
            }
            else
            {
                return task.get();
            }
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
        catch ( TimeoutException e )
        {
            throw new YampException( "task timed out after " + options.getTimeout(), e );
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
