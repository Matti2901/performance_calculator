package org.apache.commons.compress.compressors.gzip;

import java.util.Arrays;
import java.util.Objects;

/**
 * If the {@code FLG.FEXTRA} bit is set, an "extra field" is present in the header, with total length XLEN bytes. It consists of a series of subfields, each
 * of the form:
 *
 * <pre>
 * +---+---+---+---+==================================+
 * |SI1|SI2|  LEN  |... LEN bytes of subfield data ...|
 * +---+---+---+---+==================================+
 * </pre>
 * <p>
 * The reserved IDs are:
 * </p>
 *
 * <pre>
 * SI1         SI2         Data
 * ----------  ----------  ----
 * 0x41 ('A')  0x70 ('P')  Apollo file type information
 * </pre>
 * <p>
 * Subfield IDs with {@code SI2 = 0} are reserved for future use.
 * </p>
 * <p>
 * LEN gives the length of the subfield data, excluding the 4 initial bytes.
 * </p>
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc1952">RFC 1952 GZIP File Format Specification</a>
 */
public final class SubField {

    final byte si1;
    final byte si2;
    private final byte[] payload;

    public SubField(final byte si1, final byte si2, final byte[] payload) {
        this.si1 = si1;
        this.si2 = si2;
        this.payload = payload;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SubField other = (SubField) obj;
        return Arrays.equals(payload, other.payload) && si1 == other.si1 && si2 == other.si2;
    }

    /**
     * The 2 character ISO-8859-1 string made from the si1 and si2 bytes of the sub field id.
     *
     * @return Two character ID.
     */
    public String getId() {
        return String.valueOf(new char[]{(char) (si1 & 0xff), (char) (si2 & 0xff)});
    }

    /**
     * The subfield payload.
     *
     * @return The payload.
     */
    public byte[] getPayload() {
        return payload;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(payload);
        result = prime * result + Objects.hash(si1, si2);
        return result;
    }
}
