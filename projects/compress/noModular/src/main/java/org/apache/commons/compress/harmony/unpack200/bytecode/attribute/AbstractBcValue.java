package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

// Bytecode-related value (either a bytecode index or a length)
abstract class AbstractBcValue {

    int actualValue;

    public void setActualValue(final int value) {
        this.actualValue = value;
    }

}
