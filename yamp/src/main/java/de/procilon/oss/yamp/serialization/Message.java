package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.nio.ByteBuffer;

import lombok.Value;

@Value
public class Message
{
    private final int    version;
    private final String type;
    private final byte[] data;
    
    public byte[] getData()
    {
        return data.clone();
    }
    
    public ByteBuffer encode()
    {
        ByteBuffer buffer = allocate( type.length() + Integer.BYTES * 3 + data.length );
        
        ByteBuffer encodedType = TextEncoding.encode( type, US_ASCII );
        
        buffer.putInt( version )
                .putInt( encodedType.remaining() )
                .put( encodedType )
                .putInt( data.length )
                .put( data )
                .flip();
        
        return buffer;
    }
    
    public static Message decode( ByteBuffer binary )
    {
        int version = binary.getInt();
        
        int typeLen = binary.getInt();
        ByteBuffer encodedType = binary.slice();
        encodedType.limit( typeLen );
        String type = TextEncoding.decode( encodedType, US_ASCII );
        
        binary.position( binary.position() + typeLen );
        int dataLen = binary.getInt();
        
        if ( dataLen > binary.remaining() )
        {
            throw new IllegalArgumentException( "size out of bounds" );
        }
        byte[] data = new byte[dataLen];
        binary.get( data );
        
        return new Message( version, type, data );
    }
}
