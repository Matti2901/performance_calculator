package org.apache.commons.compress.archivers.sevenz.delagating;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Deflater;

public final class DelegatingDeflater extends Deflater {

    private final Deflater deflater;

    public final AtomicBoolean isEnded = new AtomicBoolean();

    public DelegatingDeflater(final Deflater deflater) {
        this.deflater = deflater;
    }

    @Override
    public int deflate(final byte[] b) {
        return deflater.deflate(b);
    }

    @Override
    public int deflate(final byte[] b, final int off, final int len) {
        return deflater.deflate(b, off, len);
    }

    @Override
    public int deflate(final byte[] b, final int off, final int len, final int flush) {
        return deflater.deflate(b, off, len, flush);
    }

    @Override
    public void end() {
        isEnded.set(true);
        deflater.end();
    }

    @Override
    public void finish() {
        deflater.finish();
    }

    @Override
    public boolean finished() {
        return deflater.finished();
    }

    @Override
    public int getAdler() {
        return deflater.getAdler();
    }

    @Override
    public long getBytesRead() {
        return deflater.getBytesRead();
    }

    @Override
    public long getBytesWritten() {
        return deflater.getBytesWritten();
    }

    @Override
    public int getTotalIn() {
        return deflater.getTotalIn();
    }

    @Override
    public int getTotalOut() {
        return deflater.getTotalOut();
    }

    @Override
    public boolean needsInput() {
        return deflater.needsInput();
    }

    @Override
    public void reset() {
        deflater.reset();
    }

    @Override
    public void setDictionary(final byte[] b) {
        deflater.setDictionary(b);
    }

    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
        deflater.setDictionary(b, off, len);
    }

    @Override
    public void setInput(final byte[] b) {
        deflater.setInput(b);
    }

    @Override
    public void setInput(final byte[] b, final int off, final int len) {
        deflater.setInput(b, off, len);
    }

    @Override
    public void setLevel(final int level) {
        deflater.setLevel(level);
    }

    @Override
    public void setStrategy(final int strategy) {
        deflater.setStrategy(strategy);
    }

}
