package org.apache.jackrabbit.spi2dav;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

public abstract class AbstractRepositoryServiceImpl {
	public abstract IdFactory getIdFactory();
	public abstract HttpResponse executeRequest(SessionInfo sessionInfo, HttpUriRequest request) throws IOException, RepositoryException;
	protected abstract NamePathResolver getNamePathResolver(SessionInfo sessionInfo) throws RepositoryException;
	public abstract PathFactory getPathFactory();
	abstract String getUniqueID(DavPropertySet propSet);
	abstract Name getQName(DavPropertySet propSet, NamePathResolver resolver) throws RepositoryException;
	abstract int getIndex(DavPropertySet propSet);
}
