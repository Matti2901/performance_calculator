package org.apache.commons.compress.compressors.snappy;

enum State {
    NO_BLOCK, IN_LITERAL, IN_BACK_REFERENCE
}
