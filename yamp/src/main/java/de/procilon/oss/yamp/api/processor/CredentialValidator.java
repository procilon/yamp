package de.procilon.oss.yamp.api.processor;

import java.nio.ByteBuffer;

import de.procilon.oss.yamp.api.shared.RequestContext;

public interface CredentialValidator
{
    boolean validate( ByteBuffer message, ByteBuffer credentialData, RequestContext context );
}
