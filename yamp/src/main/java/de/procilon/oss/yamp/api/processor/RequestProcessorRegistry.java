package de.procilon.oss.yamp.api.processor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import de.procilon.oss.yamp.api.shared.Request;
import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.api.shared.Response;
import de.procilon.oss.yamp.serialization.Message;

public enum RequestProcessorRegistry implements MessageProcessor
{
    INSTANCE;
    
    Map<String, Function<Message, ?>>   decoders   = new ConcurrentHashMap<>();
    Map<String, RequestProcessor<?, ?>> processors = new ConcurrentHashMap<>();
    
    public <T extends Request<?>> void register( String type, RequestProcessor<T, ?> processor, Function<Message, T> requestDecoder )
    {
        decoders.put( type, requestDecoder );
        processors.put( type, processor );
    }
    
    @SuppressWarnings( "unchecked" )
    public <T extends Request<?>> void register( RequestProcessor<T, ?> processor, Function<Message, T> requestDecoder )
    {
        Class<T> type = null;
        for ( Type t : processor.getClass().getGenericInterfaces() )
        {
            if ( t instanceof ParameterizedType )
            {
                ParameterizedType pt = (ParameterizedType) t;
                if ( pt.getRawType() == RequestProcessor.class )
                {
                    type = (Class<T>) pt.getActualTypeArguments()[0];
                }
            }
        }
        
        if ( type == null )
        {
            throw new IllegalArgumentException( "failed to determine request type of " + processor.getClass() );
        }
        
        register( type.getName(), processor, requestDecoder );
    }
    
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Override
    public Message process( Message message, RequestContext context )
    {
        String type = message.getType();
        Function<Message, ?> decoder = decoders.get( type );
        if ( decoder == null )
        {
            throw new IllegalArgumentException( "unknown message type " + type );
        }
        
        Request request = (Request) decoder.apply( message );
        RequestProcessor processor = processors.get( type );
        
        Response response = processor.process( request, context );
        
        return response.toMessage();
    }
    
    @Override
    public List<String> types()
    {
        return new ArrayList<>( processors.keySet() );
    }
    
    public static Optional<MessageProcessor> forMessageType( String type )
    {
        if ( INSTANCE.decoders.containsKey( type ) )
        {
            return Optional.of( INSTANCE );
        }
        else
        {
            return Optional.empty();
        }
    }
}
