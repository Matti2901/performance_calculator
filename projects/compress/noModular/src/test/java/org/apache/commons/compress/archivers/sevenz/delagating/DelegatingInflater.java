package org.apache.commons.compress.archivers.sevenz.delagating;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public final class DelegatingInflater extends Inflater {

    private final Inflater inflater;

    public final AtomicBoolean isEnded = new AtomicBoolean();

    public DelegatingInflater(final Inflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public void end() {
        isEnded.set(true);
        inflater.end();
    }

    @Override
    public boolean finished() {
        return inflater.finished();
    }

    @Override
    public int getAdler() {
        return inflater.getAdler();
    }

    @Override
    public long getBytesRead() {
        return inflater.getBytesRead();
    }

    @Override
    public long getBytesWritten() {
        return inflater.getBytesWritten();
    }

    @Override
    public int getRemaining() {
        return inflater.getRemaining();
    }

    @Override
    public int getTotalIn() {
        return inflater.getTotalIn();
    }

    @Override
    public int getTotalOut() {
        return inflater.getTotalOut();
    }

    @Override
    public int inflate(final byte[] b) throws DataFormatException {
        return inflater.inflate(b);
    }

    @Override
    public int inflate(final byte[] b, final int off, final int len) throws DataFormatException {
        return inflater.inflate(b, off, len);
    }

    @Override
    public boolean needsDictionary() {
        return inflater.needsDictionary();
    }

    @Override
    public boolean needsInput() {
        return inflater.needsInput();
    }

    @Override
    public void reset() {
        inflater.reset();
    }

    @Override
    public void setDictionary(final byte[] b) {
        inflater.setDictionary(b);
    }

    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
        inflater.setDictionary(b, off, len);
    }

    @Override
    public void setInput(final byte[] b) {
        inflater.setInput(b);
    }

    @Override
    public void setInput(final byte[] b, final int off, final int len) {
        inflater.setInput(b, off, len);
    }

}
