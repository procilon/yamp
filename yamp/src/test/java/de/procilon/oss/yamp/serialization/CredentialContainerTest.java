package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

class CredentialContainerTest
{
    @Test
    void encodeDecode()
    {
        String type = "test-credential";
        ByteBuffer value = wrap( "secret".getBytes( US_ASCII ) );
        CredentialContainer credentialContainer = new CredentialContainer( type, value.slice() );
        
        ByteBuffer encoded = allocate( credentialContainer.size() );
        
        credentialContainer.encodeTo( encoded );
        assertFalse( encoded.hasRemaining() );
        
        encoded.flip();
        CredentialContainer decoded = CredentialContainer.decode( encoded );
        
        assertThat( decoded.getType(), is( type ) );
        assertThat( decoded.getValue(), is( value ) );
    }
    
}
