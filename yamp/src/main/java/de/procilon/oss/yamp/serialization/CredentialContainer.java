package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor
public class CredentialContainer
{
    @NonNull
    private final String     type;
    @NonNull
    private final ByteBuffer value;
    
    public CredentialContainer( String type, byte[] value )
    {
        this( type, wrap( value ) );
    }
    
    public ByteBuffer getValue()
    {
        return value.slice();
    }
    
    public int size()
    {
        return Integer.BYTES * 2 + type.length() + value.remaining();
    }
    
    public ByteBuffer encodeTo( ByteBuffer buffer )
    {
        ByteBuffer encodedType = UTF_8.encode( type );
        
        buffer.putInt( encodedType.remaining() );
        buffer.put( encodedType );
        buffer.putInt( value.remaining() );
        buffer.put( value.slice() );
        
        return buffer;
    }
    
    public static CredentialContainer decode( ByteBuffer data )
    {
        int typeLen = data.getInt();
        data.limit( data.position() + typeLen );
        String type = UTF_8.decode( data ).toString();
        
        data.limit( data.capacity() );
        int length = data.getInt();
        data.limit( data.position() + length );
        
        return new CredentialContainer( type, data.slice() );
    }
}
