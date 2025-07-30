/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.commons.compress.archivers.zip;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;

/**
 * Static helper functions for robustly encoding file names in ZIP files.
 */
public abstract class ZipEncodingHelper {

    /**
     * UTF-8.
     */
    static final ZipEncoding ZIP_ENCODING_UTF_8 = getZipEncoding(UTF_8);

    /**
     * Instantiates a ZIP encoding. An NIO based character set encoder/decoder will be returned. As a special case, if the character set is UTF-8, the NIO
     * encoder will be configured replace malformed and unmappable characters with '?'. This matches existing behavior from the older fallback encoder.
     * <p>
     * If the requested character set cannot be found, the platform default will be used instead.
     * </p>
     *
     * @param charset The charset of the ZIP encoding. Specify {@code null} for the platform's default encoding.
     * @return A ZIP encoding for the given encoding name.
     * @since 1.26.0
     */
    public static ZipEncoding getZipEncoding(final Charset charset) {
        return new NioZipEncoding(Charsets.toCharset(charset));
    }

    /**
     * Instantiates a ZIP encoding. An NIO based character set encoder/decoder will be returned. As a special case, if the character set is UTF-8, the NIO
     * encoder will be configured replace malformed and unmappable characters with '?'. This matches existing behavior from the older fallback encoder.
     * <p>
     * If the requested character set cannot be found, the platform default will be used instead.
     * </p>
     *
     * @param name The name of the ZIP encoding. Specify {@code null} for the platform's default encoding.
     * @return A ZIP encoding for the given encoding name.
     */
    public static ZipEncoding getZipEncoding(final String name) {
        return new NioZipEncoding(toSafeCharset(name));
    }

    /**
     * Returns a Charset for the named charset. If the name cannot find a charset, return {@link Charset#defaultCharset()}.
     *
     * @param name The name of the requested charset, may be null.
     * @return a Charset for the named charset.
     * @see Charset#defaultCharset()
     */
    private static Charset toSafeCharset(final String name) {
        try {
            return Charsets.toCharset(name);
        } catch (final IllegalArgumentException | NullPointerException ignored) {
            return Charset.defaultCharset();
        }
    }
}
