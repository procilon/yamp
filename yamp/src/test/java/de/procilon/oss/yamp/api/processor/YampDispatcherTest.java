package de.procilon.oss.yamp.api.processor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import de.procilon.oss.yamp.api.shared.Encodable;
import de.procilon.oss.yamp.api.shared.Request;
import de.procilon.oss.yamp.api.shared.RequestContext;
import de.procilon.oss.yamp.api.shared.Response;
import de.procilon.oss.yamp.serialization.CredentialContainer;
import de.procilon.oss.yamp.serialization.ErrorMessage;
import de.procilon.oss.yamp.serialization.Message;
import de.procilon.oss.yamp.serialization.RequestContainer;
import de.procilon.oss.yamp.serialization.ResponseContainer;
import lombok.Value;

class YampDispatcherTest
{
    
    @Test
    void happyPath()
    {
        String input = "abcd";
        String expected = "dcba";
        CredentialValidator authenticator = new AllowAll();
        
        RequestProcessorRegistry.INSTANCE.register( new TestRequestProcessor(), TestRequest::decode );
        
        Function<String, Optional<CredentialValidator>> validatorRegistry = s -> Optional.of( authenticator );
        YampDispatcher dispatcher = new YampDispatcher( validatorRegistry, RequestProcessorRegistry::forMessageType );
        
        RequestContainer request = new RequestContainer( new TestRequest( input ).toMessage().encode(),
                new CredentialContainer( "", new byte[0] ) );
        
        ByteBuffer response = dispatcher.process( request.encode() );
        
        ResponseContainer responseContainer = ResponseContainer.decode( response );
        Message message = Message.decode( responseContainer.getMessage() );
        String receivedResponse = TestResponse.decode( message ).getMessage();
        
        assertThat( receivedResponse, is( expected ) );
    }
    
    @Test
    void customValidationError()
    {
        String input = "irrelevant";
        CredentialValidator authenticator = new DenyAll();
        
        YampDispatcherErrors errors = new YampDispatcherErrors()
        {
            @Override
            public RuntimeException credentialValidationFailed( RequestContext context )
            {
                return new TestException();
            }
        };
        
        Function<String, Optional<CredentialValidator>> validatorRegistry = s -> Optional.of( authenticator );
        YampDispatcher dispatcher = new YampDispatcher( validatorRegistry, RequestProcessorRegistry::forMessageType, errors, false );
        
        RequestContainer request = new RequestContainer( new TestRequest( input ).toMessage().encode(),
                new CredentialContainer( "", new byte[0] ) );
        
        ByteBuffer rawResponse = dispatcher.process( request.encode() );
        
        ResponseContainer response = ResponseContainer.rawDecode( rawResponse );
        assertThat( response.isError(), is( true ) );
        
        ErrorMessage errorMessage = ErrorMessage.decode( response.getMessage() );
        assertThat( errorMessage.getType(), is( TestException.class.getName() ) );
    }
    
    class TestException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }
    
    class AllowAll implements CredentialValidator
    {
        @Override
        public boolean validate( ByteBuffer message, ByteBuffer credentialData, RequestContext context )
        {
            return true;
        }
    }
    
    class DenyAll implements CredentialValidator
    {
        @Override
        public boolean validate( ByteBuffer message, ByteBuffer credentialData, RequestContext context )
        {
            return false;
        }
    }
    
    class TestRequestProcessor implements RequestProcessor<TestRequest, TestResponse>
    {
        @Override
        public TestResponse process( TestRequest request, RequestContext context )
        {
            String message = request.getMessage();
            return new TestResponse( new StringBuilder( message ).reverse().toString() );
        }
    }
    
    @Value
    static class TestRequest implements Request<TestResponse>
    {
        String message;
        
        @Override
        public byte[] encode()
        {
            return message.getBytes( UTF_8 );
        }
        
        public static TestRequest decode( Message m )
        {
            assert m.getType().equals( TestRequest.class.getName() );
            assert m.getVersion() == Encodable.INITIAL_VERSION;
            
            return new TestRequest( new String( m.getData(), UTF_8 ) );
        }
    }
    
    @Value
    static class TestResponse implements Response
    {
        String message;
        
        @Override
        public byte[] encode()
        {
            return message.getBytes( UTF_8 );
        }
        
        public static TestResponse decode( Message m )
        {
            assert m.getType().equals( TestResponse.class.getName() );
            assert m.getVersion() == Encodable.INITIAL_VERSION;
            
            return new TestResponse( new String( m.getData(), UTF_8 ) );
        }
    }
}
