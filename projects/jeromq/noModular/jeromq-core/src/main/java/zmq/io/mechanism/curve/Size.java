package zmq.io.mechanism.curve;

import com.neilalexander.jnacl.crypto.curve25519xsalsa20poly1305;

enum Size {
    NONCE {
        @Override
        public int bytes() {
            return curve25519xsalsa20poly1305.crypto_secretbox_NONCEBYTES;
        }
    },
    ZERO {
        @Override
        public int bytes() {
            return curve25519xsalsa20poly1305.crypto_secretbox_ZEROBYTES;
        }
    },
    BOXZERO {
        @Override
        public int bytes() {
            return curve25519xsalsa20poly1305.crypto_secretbox_BOXZEROBYTES;
        }
    },
    PUBLICKEY {
        @Override
        public int bytes() {
            return curve25519xsalsa20poly1305.crypto_secretbox_PUBLICKEYBYTES;
        }
    },
    SECRETKEY {
        @Override
        public int bytes() {
            return curve25519xsalsa20poly1305.crypto_secretbox_SECRETKEYBYTES;
        }
    },
    KEY {
        @Override
        public int bytes() {
            return 32;
        }
    },
    BEFORENM {
        @Override
        public int bytes() {
            return curve25519xsalsa20poly1305.crypto_secretbox_BEFORENMBYTES;
        }
    };

    public abstract int bytes();
}
