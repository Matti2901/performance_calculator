package org.apache.commons.compress;

import java.io.InputStream;

public interface StreamWrapper<I extends InputStream> {
    I wrap(InputStream inputStream) throws Exception;
}
