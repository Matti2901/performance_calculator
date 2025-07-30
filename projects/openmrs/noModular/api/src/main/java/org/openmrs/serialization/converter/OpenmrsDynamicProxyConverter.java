/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.serialization.converter;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.extended.DynamicProxyConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * An instance of this converter needs to be registered with a higher priority than the rest so
 * that it's called early in the converter chain. This way, we can make sure we never get to
 * xstream's DynamicProxyConverter that can deserialize proxies.
 *
 * @see <a href="http://tinyurl.com/ord2rry">this blog</a>
 */
public class OpenmrsDynamicProxyConverter extends DynamicProxyConverter {

	public OpenmrsDynamicProxyConverter() {
		super(null);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		throw new XStreamException("Can't serialize proxies");
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		throw new XStreamException("Can't deserialize proxies");
	}

}
