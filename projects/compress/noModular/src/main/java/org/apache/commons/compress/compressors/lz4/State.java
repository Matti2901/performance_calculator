package org.apache.commons.compress.compressors.lz4;

public enum State {
    NO_BLOCK, IN_LITERAL, LOOKING_FOR_BACK_REFERENCE, IN_BACK_REFERENCE, EOF
}
