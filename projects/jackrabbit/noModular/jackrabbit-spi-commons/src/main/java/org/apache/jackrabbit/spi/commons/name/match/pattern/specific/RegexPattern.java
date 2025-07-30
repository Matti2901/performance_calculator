package org.apache.jackrabbit.spi.commons.name.match.pattern.specific;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.match.pattern.Pattern;

public class RegexPattern extends Pattern.AbstractNamePattern {
    private final java.util.regex.Pattern namespaceUri;
    private final java.util.regex.Pattern localName;
    private final String localNameStr;
    private final String namespaceUriStr;

    public RegexPattern(String namespaceUri, String localName) {
        super();

        this.namespaceUri = java.util.regex.Pattern.compile(namespaceUri);
        this.localName = java.util.regex.Pattern.compile(localName);
        this.namespaceUriStr = namespaceUri;
        this.localNameStr = localName;
    }

    protected boolean matches(Path.Element element) {
        Name name = element.getName();
        boolean nsMatches = namespaceUri.matcher(name.getNamespaceURI()).matches();
        boolean localMatches = localName.matcher(name.getLocalName()).matches();
        return nsMatches && localMatches;
    }

    public String toString() {
        return new StringBuffer()
                .append("\"{")
                .append(namespaceUriStr)
                .append("}")
                .append(localNameStr)
                .append("\"")
                .toString();
    }
}
