package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.allocate;

import java.nio.ByteBuffer;

import de.procilon.oss.yamp.api.caller.PeerException;
import lombok.Value;

@Value
public class ResponseContainer
{
    boolean    error;
    ByteBuffer message;
    
    public static ResponseContainer ofSuccess( ByteBuffer response )
    {
        return new ResponseContainer( false, response );
    }
    
    public static ResponseContainer ofError( Throwable t )
    {
        return new ResponseContainer( true, ErrorMessage.fromThrowable( t ).encode() );
    }
    
    public ByteBuffer getMessage()
    {
        return message.slice();
    }
    
    public ByteBuffer encode()
    {
        ByteBuffer buffer = allocate( 1 + Integer.BYTES + message.remaining() );
        
        buffer.put( (byte) (error ? 1 : 0) )
                .putInt( message.remaining() )
                .put( message.slice() )
                .flip();
        
        return buffer;
    }
    
    public static ResponseContainer decode( ByteBuffer encoded )
    {
        boolean error = encoded.get() != 0;
        
        int length = encoded.getInt();
        ByteBuffer message = encoded.slice();
        message.limit( length );
        
        encoded.position( encoded.position() + length );
        
        if ( error )
        {
            ErrorMessage errorMessage = ErrorMessage.decode( message );
            
            throw new PeerException( errorMessage.getType(), errorMessage.getMessage() );
        }
        else
        {
            
            return ofSuccess( message );
        }
    }
}
