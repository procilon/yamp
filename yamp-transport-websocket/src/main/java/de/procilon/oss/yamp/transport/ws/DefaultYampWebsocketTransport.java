package de.procilon.oss.yamp.transport.ws;

import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * a slightly simplified {@link YampJavaxWebsocketTransport} that is optimal for most simple use-cases. Since it is already annotated it can
 * create trouble in more complex szenarios in which case {@link YampJavaxWebsocketTransport} should be used directly.
 * 
 * @author fichtelmannm
 *
 */
public class DefaultYampWebsocketTransport extends YampJavaxWebsocketTransport
{
    @OnOpen
    @Override
    public void onOpen( Session session )
    {
        super.onOpen( session );
    }
    
    @OnClose
    @Override
    public void onClose( Session session )
    {
        super.onClose( session );
    }
    
    @OnMessage
    @Override
    public void onMessage( ByteBuffer message )
    {
        super.onMessage( message );
    }
}
