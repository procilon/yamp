package de.procilon.oss.yamp.api.shared;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext
{
    private final Map<Class<?>, Object> contextData = new ConcurrentHashMap<>();
    
    public <T> void put( Class<T> type, T value )
    {
        contextData.put( type, value );
    }
    
    @SuppressWarnings( "unchecked" )
    public <T> Optional<T> find( Class<T> type )
    {
        return Optional.ofNullable( (T) contextData.get( type ) );
    }
}
