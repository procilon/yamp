package de.procilon.oss.yamp.api.processor;

import java.util.List;

import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.serialization.Message;

public interface MessageProcessor
{
    Message process( Message message, RequestContext context );
    
    List<String> types();
}
