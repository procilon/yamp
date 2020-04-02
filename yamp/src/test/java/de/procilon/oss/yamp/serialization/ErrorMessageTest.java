package de.procilon.oss.yamp.serialization;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

class ErrorMessageTest
{
    @Test
    void encodeDecode() throws Exception
    {
        String type = "test-type";
        String message = "test-message";
        ErrorMessage errorMessage = new ErrorMessage( type, message );
        
        ByteBuffer encoded = errorMessage.encode();
        ErrorMessage decoded = ErrorMessage.decode( encoded );
        
        assertThat( decoded.getType(), is( type ) );
        assertThat( decoded.getMessage(), is( message ) );
    }
    
    @Test
    void failOnEncodeNonasciiType() throws Exception
    {
        assertThrows( IllegalArgumentException.class, () -> new ErrorMessage( "töst", "irrelevant" ).encode() );
    }
    
    @Test
    void succeedToEncodeNonasciiMessage() throws Exception
    {
        ByteBuffer encoded = new ErrorMessage( "irrelevant", "töst" ).encode();
        
        assertThat( encoded, is( not( nullValue() ) ) );
    }
}
