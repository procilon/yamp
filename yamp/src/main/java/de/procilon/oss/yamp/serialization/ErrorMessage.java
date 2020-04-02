package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;

import lombok.Value;

@Value
public class ErrorMessage
{
    String type;
    String message;
    
    public static ErrorMessage fromThrowable( Throwable t )
    {
        return new ErrorMessage( t.getClass().getName(), t.getMessage() );
    }
    
    public ByteBuffer encode()
    {
        ByteBuffer type = TextEncoding.encode( this.type, US_ASCII );
        ByteBuffer message = TextEncoding.encode( this.message, UTF_8 );
        
        ByteBuffer buffer = allocate( Integer.BYTES * 2 + type.remaining() + message.remaining() );
        
        buffer.putInt( type.remaining() )
                .put( type )
                .putInt( message.remaining() )
                .put( message )
                .flip();
        
        return buffer.asReadOnlyBuffer();
    }
    
    public static ErrorMessage decode( ByteBuffer buffer )
    {
        int originalLimit = buffer.limit();
        
        int typeLength = buffer.getInt();
        buffer.limit( buffer.position() + typeLength );
        String type = TextEncoding.decode( buffer, US_ASCII );
        buffer.limit( originalLimit );
        
        int messageLength = buffer.getInt();
        buffer.limit( buffer.position() + messageLength );
        String message = TextEncoding.decode( buffer, UTF_8 );
        buffer.limit( originalLimit );
        
        return new ErrorMessage( type, message );
    }
}
