package de.procilon.oss.yamp.serialization;

import static java.nio.ByteBuffer.allocate;

import java.nio.ByteBuffer;

import lombok.NonNull;
import lombok.Value;

@Value
public class RequestContainer
{
    @NonNull
    ByteBuffer          message;
    @NonNull
    CredentialContainer credential;
    
    public ByteBuffer getMessage()
    {
        return message.slice();
    }
    
    public ByteBuffer encode()
    {
        ByteBuffer buffer = allocate( Integer.BYTES + message.remaining() + credential.size() );
        
        buffer.putInt( message.remaining() )
                .put( message.slice() );
        
        credential.encodeTo( buffer );
        
        buffer.flip();
        
        return buffer;
    }
    
    public static RequestContainer decode( ByteBuffer buffer )
    {
        int messageSize = buffer.getInt();
        ByteBuffer message = buffer.slice().limit( messageSize ).slice();
        buffer.position( buffer.position() + messageSize );
        
        CredentialContainer credentialContainer = CredentialContainer.decode( buffer );
        
        return new RequestContainer( message, credentialContainer );
    }
}
