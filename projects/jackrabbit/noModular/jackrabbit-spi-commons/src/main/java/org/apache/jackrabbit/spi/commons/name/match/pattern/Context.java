package org.apache.jackrabbit.spi.commons.name.match.pattern;

import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.match.MatchResult;

import javax.jcr.RepositoryException;

public class Context {
    private final Path path;
    private final int length;
    final int pos;
    private final boolean isMatch;

    public Context(Path path) {
        super();
        this.path = path;
        length = path.getLength();
        isMatch = false;
        pos = 0;
    }

    public Context(Context context, int pos, boolean matched) {
        path = context.path;
        length = context.length;
        this.pos = pos;
        this.isMatch = matched;
        if (pos > length) {
            throw new IllegalArgumentException("Cannot match beyond end of input");
        }
    }

    public Context matchToEnd() {
        return new Context(this, length, true);
    }

    public Context match(int count) {
        return new Context(this, pos + count, true);
    }

    public Context noMatch() {
        return new Context(this, this.pos, false);
    }

    public boolean isMatch() {
        return isMatch;
    }

    public Path getRemainder() throws RepositoryException {
        if (pos >= length) {
            return null;
        } else {
            return path.subPath(pos, length);
        }
    }

    public boolean isExhausted() {
        return pos == length;
    }

    public MatchResult getMatchResult() {
        return new MatchResult(path, isMatch ? pos : 0);
    }

    public String toString() {
        return pos + " @ " + path;
    }

}
