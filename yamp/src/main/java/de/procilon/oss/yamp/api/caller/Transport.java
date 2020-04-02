package de.procilon.oss.yamp.api.caller;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public interface Transport
{
    CompletionStage<ByteBuffer> transmit( ByteBuffer request );
}
