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
package org.apache.commons.compress.harmony.unpack200;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.jar.JarOutputStream;

import org.apache.commons.compress.harmony.pack200.Pack200Adapter;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.java.util.jar.Pack200.Unpacker;

/**
 * This class provides the binding between the standard Pack200 interface and the internal interface for (un)packing.
 */
public class Pack200UnpackerAdapter extends Pack200Adapter implements Unpacker {

    @Override
    public void unpack(final File file, final JarOutputStream out) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Must specify input file.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Must specify output stream.");
        }
        final long size = file.length();
        final int bufferSize = size > 0 && size < DEFAULT_BUFFER_SIZE ? (int) size : DEFAULT_BUFFER_SIZE;
        try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()), bufferSize)) {
            unpack(in, out);
        }
    }

    @Override
    public void unpack(final InputStream in, final JarOutputStream out) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("Must specify input stream.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Must specify output stream.");
        }
        completed(0);
        try {
            new Archive(in, out).unpack();
        } catch (final Pack200Exception e) {
            throw new IOException("Failed to unpack Jar:" + e);
        }
        completed(1);
    }
}
