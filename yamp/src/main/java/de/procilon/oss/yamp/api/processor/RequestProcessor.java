package de.procilon.oss.yamp.api.processor;

import de.procilon.oss.yamp.api.shared.Request;
import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.api.shared.Response;

public interface RequestProcessor<I extends Request<O>, O extends Response>
{
    O process( I request, RequestContext context );
}
