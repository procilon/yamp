package de.procilon.oss.yamp.transport.ws;

import static java.util.Collections.emptyMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.Session;

import de.procilon.oss.yamp.YampException;
import de.procilon.oss.yamp.api.caller.YampTransport;
import lombok.Getter;

/**
 * A {@link YampTransport} that transmits message over a websocket connection.
 * 
 * Usage:
 * 
 * <pre>
 * <code>
 * &#64;ServerEndoint("/yamp")
 * public class MyYampWebsocketCaller 
 *        extends YampJavaxWebsocketTransport {
 * 
 *   &#64;OnOpen
 *   public void onOpen(Session session) {
 *     super.onOpen(session);
 *   }
 * 
 *   &#64;OnClose
 *   public void onClose(Session session, CloseReason closeReason ) {
 *     super.onClose(session, closeReason);
 *   }
 * 
 *   &#64;OnMessage
 *   public void onClose(Session session, CloseReason closeReason ) {
 *     super.onMessage(session, closeReason);
 *   }
 * 
 * }
 * </code>
 * </pre>
 * 
 * or
 * 
 * <pre>
 * <code>
 * &#64;ClientEndoint
 * public class MyYampWebsocketCaller 
 *        extends YampJavaxWebsocketTransport {
 * 
 *   &#64;OnOpen
 *   public void onOpen(Session session) {
 *     super.onOpen(session);
 *   }
 * 
 *   &#64;OnClose
 *   public void onClose(Session session, CloseReason closeReason ) {
 *     super.onClose(session, closeReason);
 *   }
 * 
 *   &#64;OnMessage
 *   public void onClose(Session session, CloseReason closeReason ) {
 *     super.onMessage(session, closeReason);
 *   }
 * 
 * }
 * </code>
 * </pre>
 * 
 * @author fichtelmannm
 *
 * @see DefaultYampWebsocketTransport
 *
 */
@Getter
public class YampJavaxWebsocketTransport implements YampTransport
{
    private static final SecureRandom                                    rng               = new SecureRandom();
    
    private final Queue<Session>                                         sessions          = new ConcurrentLinkedQueue<>();
    private final Map<Long, CompletableFuture<ByteBuffer>>               pendingRequests   = new ConcurrentHashMap<>();
    private final Map<Session, Map<Long, CompletableFuture<ByteBuffer>>> requestsOnSession = new ConcurrentHashMap<>();
    
    /**
     * Adds the connected session to the queue of sessions to send messages to.
     * 
     * @param session
     *            the newly connected session.
     */
    public void onOpen( Session session )
    {
        sessions.add( session );
    }
    
    /**
     * When a session disconnects, ongoing requests on this session are completed exceptionally and the session is evicted from the queue
     * 
     * @param session
     *            the session that disconnected
     */
    public void onClose( Session session )
    {
        sessions.remove( session );
        
        Map<Long, CompletableFuture<ByteBuffer>> pendingRequests = requestsOnSession.getOrDefault( session, emptyMap() );
        
        for ( var entry : pendingRequests.entrySet() )
        {
            pendingRequests.remove( entry.getKey() );
            entry.getValue().completeExceptionally( new YampException( "peer disconnected" ) );
        }
    }
    
    /**
     * Process the provided binary message.
     * 
     * @param message
     *            the inbound binary message
     */
    public void onMessage( ByteBuffer message )
    {
        long id = message.getLong();
        
        ByteBuffer messageCopy = ByteBuffer.allocate( message.remaining() );
        messageCopy.put( message )
                .flip();
        
        CompletableFuture<ByteBuffer> response = pendingRequests.get( id );
        
        if ( response != null )
        {
            response.complete( messageCopy );
            pendingRequests.remove( id );
        } // TODO else handle missing request
    }
    
    @Override
    public CompletionStage<ByteBuffer> transmit( ByteBuffer request )
    {
        Session session = sessions.poll();
        if ( session == null )
        {
            return CompletableFuture.failedStage( new YampException( "peer not available" ) );
        }
        
        try
        {
            long requestId = rng.nextLong();
            
            ByteBuffer requestWithId = ByteBuffer.allocate( Long.BYTES + request.remaining() )
                    .putLong( requestId )
                    .put( request )
                    .flip();
            
            try
            {
                session.getBasicRemote().sendBinary( requestWithId );
            }
            catch ( IOException e )
            {
                return CompletableFuture.failedStage( e );
            }
            
            CompletableFuture<ByteBuffer> responsePromise = new CompletableFuture<>();
            pendingRequests.put( requestId, responsePromise );
            requestsOnSession.computeIfAbsent( session, s -> new ConcurrentHashMap<>() )
                    .put( requestId, responsePromise );
            
            return responsePromise;
        }
        finally
        {
            sessions.add( session );
        }
    }
}
