package de.procilon.oss.yamp.serialization;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

class TextEncoding
{
    public static ByteBuffer encode( String s, Charset charset )
    {
        try
        {
            return charset.newEncoder()
                    .onMalformedInput( CodingErrorAction.REPORT )
                    .encode( CharBuffer.wrap( s ) );
        }
        catch ( CharacterCodingException e )
        {
            throw new IllegalArgumentException( "cannot encode '" + s + "' with charset " + charset );
        }
    }
    
    public static String decode( ByteBuffer binary, Charset charset )
    {
        try
        {
            return charset.newDecoder()
                    .onMalformedInput( CodingErrorAction.REPORT )
                    .decode( binary )
                    .toString();
        }
        catch ( CharacterCodingException e )
        {
            throw new IllegalArgumentException( "cannot decode input with charset " + charset );
        }
    }
}
