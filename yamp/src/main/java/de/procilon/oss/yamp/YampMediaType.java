package de.procilon.oss.yamp;

import de.procilon.oss.yamp.serialization.RequestContainer;
import de.procilon.oss.yamp.serialization.ResponseContainer;

/**
 * YAMP specific mime types.
 * 
 * @author wolffs
 */
public final class YampMediaType
{
    /**
     * The mime type corresponding to an encoded YAMP {@link RequestContainer}
     */
    public static final String RESPONSE_CONTENT = "application/yamp-response";
    
    /**
     * The mime type corresponding to an encoded YAMP {@link ResponseContainer}
     */
    public static final String REQUEST_CONTENT  = "application/yamp-request";
}
