package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

final class BCOffset extends AbstractBcValue {

    public final int offset;
    public int index;

    BCOffset(final int offset) {
        this.offset = offset;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

}
