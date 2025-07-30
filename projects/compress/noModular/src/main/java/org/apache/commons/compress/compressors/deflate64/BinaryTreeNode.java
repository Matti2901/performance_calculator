package org.apache.commons.compress.compressors.deflate64;

final class BinaryTreeNode {
    private final int bits;
    int literal = -1;
    BinaryTreeNode leftNode;
    BinaryTreeNode rightNode;

    BinaryTreeNode(final int bits) {
        this.bits = bits;
    }

    void leaf(final int symbol) {
        literal = symbol;
        leftNode = null;
        rightNode = null;
    }

    BinaryTreeNode left() {
        if (leftNode == null && literal == -1) {
            leftNode = new BinaryTreeNode(bits + 1);
        }
        return leftNode;
    }

    BinaryTreeNode right() {
        if (rightNode == null && literal == -1) {
            rightNode = new BinaryTreeNode(bits + 1);
        }
        return rightNode;
    }
}
