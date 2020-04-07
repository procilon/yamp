package de.procilon.oss.yamp.transport.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

import de.procilon.oss.yamp.YampMediaType;
import de.procilon.oss.yamp.api.caller.YampTransport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpTransport implements YampTransport
{
    private final URI        remoteEndpoint;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    @Override
    public CompletionStage<ByteBuffer> transmit( ByteBuffer request )
    {
        byte[] requestData = new byte[request.remaining()];
        request.get( requestData );
        
        HttpRequest httpRequest = HttpRequest.newBuilder( remoteEndpoint )
                .POST( BodyPublishers.ofByteArray( requestData ) )
                .header( "Content-Type", YampMediaType.REQUEST_CONTENT )
                .header( "Accept", YampMediaType.RESPONSE_CONTENT )
                .build();
        
        // TODO handle non-yamp responses
        
        return httpClient.sendAsync( httpRequest, BodyHandlers.ofByteArray() )
                .thenApply( HttpResponse::body )
                .thenApply( ByteBuffer::wrap );
    }
}
