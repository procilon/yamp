package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

class RequestContainerTest
{
    
    @Test
    void encodeDecode()
    {
        ByteBuffer message = wrap( "test message".getBytes( US_ASCII ) );
        CredentialContainer credential = new CredentialContainer( "TestCredential", "secret".getBytes( US_ASCII ) );
        RequestContainer requestContainer = new RequestContainer( message.slice(), credential );
        
        ByteBuffer encoded = requestContainer.encode();
        RequestContainer decoded = RequestContainer.decode( encoded );
        
        assertThat( decoded.getMessage(), is( equalTo( message ) ) );
        assertThat( decoded.getCredential(), is( equalTo( credential ) ) );
    }
    
}
