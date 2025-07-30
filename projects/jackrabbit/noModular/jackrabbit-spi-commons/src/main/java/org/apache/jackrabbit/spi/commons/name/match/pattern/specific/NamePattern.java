package org.apache.jackrabbit.spi.commons.name.match.pattern.specific;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.match.pattern.Pattern;

public class NamePattern extends Pattern.AbstractNamePattern {
    private final Name name;

    public NamePattern(Name name) {
        super();
        this.name = name;
    }

    protected boolean matches(Path.Element element) {
        return name.equals(element.getName());
    }

    public String toString() {
        return new StringBuffer()
                .append("\"")
                .append(name)
                .append("\"")
                .toString();
    }
}
