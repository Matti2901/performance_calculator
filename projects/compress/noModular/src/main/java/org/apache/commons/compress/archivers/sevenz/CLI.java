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

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Usage: archive-name [list]
 */
public class CLI {

    private static Mode grabMode(final String[] args) {
        if (args.length < 2) {
            return Mode.LIST;
        }
        return Enum.valueOf(Mode.class, args[1].toUpperCase(Locale.ROOT));
    }

    /**
     * Command line entry point.
     *
     * @param args Command line arguments.
     * @throws IOException if an I/O problem occurs.
     */
    public static void main(final String[] args) throws IOException {
        if (ArrayUtils.isEmpty(args)) {
            usage();
            return;
        }
        final Mode mode = grabMode(args);
        System.out.println(mode.getMessage() + " " + args[0]);
        final File file = new File(args[0]);
        if (!file.isFile()) {
            System.err.println(file + " doesn't exist or is a directory");
        }
        try (SevenZFile archive = SevenZFile.builder().setFile(file).get()) {
            SevenZArchiveEntry ae;
            while ((ae = archive.getNextEntry()) != null) {
                mode.takeAction(archive, ae);
            }
        }
    }

    private static void usage() {
        System.out.println("Parameters: archive-name [list]");
    }

}
