package de.procilon.oss.yamp.serialization;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**
 * Internal utility class that handles encoding/decoding of text in a strict manner.
 * 
 * @author fichtelmannm
 *
 */
class TextEncoding
{
    /**
     * Similar to {@link Charset#encode(String)} but fails if a character cannot be encoded in the specified charset.
     * 
     * @param s
     *            the text to encode
     * @param charset
     *            the {@link Charset} to encode with
     * @return the encoded binary of the input string in the specified charset
     */
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
    
    /**
     * Similar to {@link Charset#decode(ByteBuffer)} but fails if a character cannot be decoded in the specified charset.
     * 
     * @param binary
     *            the binary to decode
     * @param charset
     *            the {@link Charset} to decode with
     * @return the decoded text
     */
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
