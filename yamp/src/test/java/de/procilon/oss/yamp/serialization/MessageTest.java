package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

class MessageTest
{
    
    @Test
    void encodeDecode()
    {
        int version = 42;
        String type = "TestMessage";
        byte[] data = "test data".getBytes( US_ASCII );
        
        Message message = new Message( version, type, data );
        
        ByteBuffer encoded = message.encode();
        Message decoded = Message.decode( encoded );
        
        assertThat( decoded.getVersion(), is( version ) );
        assertThat( decoded.getType(), is( type ) );
        assertThat( decoded.getData(), is( data ) );
    }
    
    @Test
    void doNotAllocateOnMaliciousMessage() throws Exception
    {
        ByteBuffer buffer = allocate( 100 );
        
        buffer.putInt( 1 )
                .putInt( 4 )
                .put( "test".getBytes( US_ASCII ) )
                .putInt( 1000 )
                .put( new byte[10] )
                .flip();
        
        assertThrows( IllegalArgumentException.class, () -> Message.decode( buffer ) );
    }
}
