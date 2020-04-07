package de.procilon.oss.yamp.transport.reversehttp;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

import de.procilon.oss.yamp.api.caller.YampTransport;

public class ReverseHttpTransport implements YampTransport
{
    @Override
    public CompletionStage<ByteBuffer> transmit( ByteBuffer request )
    {
        // TODO NYI
        throw new UnsupportedOperationException();
    }
}
