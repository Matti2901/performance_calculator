package org.apache.commons.compress.compressors.zstandard;

import org.apache.commons.io.function.IOFunction;

import java.io.FileOutputStream;

interface OutputStreamCreator extends IOFunction<FileOutputStream, ZstdCompressorOutputStream> {
    // empty
}
