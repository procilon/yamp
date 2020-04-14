package de.procilon.oss.yamp.transport.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import de.procilon.oss.yamp.YampException;
import de.procilon.oss.yamp.YampMediaType;
import de.procilon.oss.yamp.api.caller.YampTransport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpTransport implements YampTransport
{
    private final URI        remoteEndpoint;
    private final HttpClient httpClient;
    
    public HttpTransport( URI remoteEndpoint )
    {
        this( remoteEndpoint, HttpClient.newHttpClient() );
    }
    
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
        
        return httpClient.sendAsync( httpRequest, BodyHandlers.ofByteArray() )
                .thenApply( HttpTransport::body )
                .thenApply( ByteBuffer::wrap );
    }
    
    private static <T> T body( HttpResponse<T> response )
    {
        String contentType = response.headers().firstValue( "Content-Type" ).orElse( "" );
        if ( YampMediaType.RESPONSE_CONTENT.equals( contentType ) )
        {
            return response.body();
        }
        else
        {
            throw new IllegalArgumentException( "expected response of type " + YampMediaType.RESPONSE_CONTENT );
        }
    }
    
    public static HttpTransport authenticatedTlsTransport( URI remoteEndpoint, KeyStore truststore, KeyStore keystore, char[] keyPassword )
    {
        try
        {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
            kmf.init( keystore, keyPassword );
            TrustManagerFactory tmf = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
            tmf.init( truststore );
            
            SSLContext tlsContext = SSLContext.getInstance( "TLSv1.2" );
            tlsContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstanceStrong() );
            
            HttpClient httpClient = HttpClient.newBuilder()
                    .sslContext( tlsContext )
                    .build();
            
            return new HttpTransport( remoteEndpoint, httpClient );
        }
        catch ( GeneralSecurityException e )
        {
            throw new YampException( "failed to create TLS transport", e );
        }
    }
}
