package org.apache.jackrabbit.spi.commons.value;

import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;

public interface QValueParameter {
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();
    public static final NameFactory NAME_FACTORY = NameFactoryImpl.getInstance();
}
