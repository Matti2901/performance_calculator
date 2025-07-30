package org.apache.commons.compress.compressors.lz4;

import java.io.InputStream;

interface StreamWrapper {
    InputStream wrap(InputStream in) throws Exception;
}
