package org.apache.jackrabbit.spi2jcr;

import javax.jcr.Session;

import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;

public interface SessionEventInterface {

	Session getSession();

	NamePathResolver getNamePathResolver();

	void removeSubscription(EventSubscription eventSubscription);

}
