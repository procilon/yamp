package de.procilon.oss.yamp.api.caller;

import java.time.Duration;

import lombok.Value;

@Value
public class YampOptions
{
    public static final YampOptions EMPTY = new YampOptions( null );
    
    Duration                        timeout;
}
