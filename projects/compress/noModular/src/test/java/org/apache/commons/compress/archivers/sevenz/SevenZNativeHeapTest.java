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
package org.apache.commons.compress.archivers.sevenz;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.compress.archivers.sevenz.coder.Coders;
import org.apache.commons.compress.archivers.sevenz.delagating.DelegatingDeflater;
import org.apache.commons.compress.archivers.sevenz.delagating.DelegatingInflater;
import org.apache.commons.compress.general.AbstractTest;
import org.apache.commons.compress.archivers.sevenz.coder.Coders.DeflateDecoder;
import org.apache.commons.compress.archivers.sevenz.coder.io.DeflateDecoderInputStream;
import org.apache.commons.compress.archivers.sevenz.coder.io.DeflateDecoderOutputStream;
import org.apache.commons.compress.utils.ByteUtils;
import org.junit.jupiter.api.Test;

class SevenZNativeHeapTest extends AbstractTest {

    @Test
    void testEndDeflaterOnCloseStream() throws Exception {
        final Coders.DeflateDecoder deflateDecoder = new DeflateDecoder();
        final DelegatingDeflater delegatingDeflater;
        try (DeflateDecoderOutputStream outputStream = (DeflateDecoderOutputStream) deflateDecoder.encode(new ByteArrayOutputStream(), 9)) {
            delegatingDeflater = new DelegatingDeflater(outputStream.deflater);
            outputStream.deflater = delegatingDeflater;
        }
        assertTrue(delegatingDeflater.isEnded.get());

    }

    @Test
    void testEndInflaterOnCloseStream() throws Exception {
        final Coders.DeflateDecoder deflateDecoder = new DeflateDecoder();
        final DelegatingInflater delegatingInflater;
        try (DeflateDecoderInputStream inputStream = (DeflateDecoderInputStream) deflateDecoder.decode("dummy",
                new ByteArrayInputStream(ByteUtils.EMPTY_BYTE_ARRAY), 0, null, null, Integer.MAX_VALUE)) {
            delegatingInflater = new DelegatingInflater(inputStream.inflater);
            inputStream.inflater = delegatingInflater;
        }

        assertTrue(delegatingInflater.isEnded.get());
    }
}
