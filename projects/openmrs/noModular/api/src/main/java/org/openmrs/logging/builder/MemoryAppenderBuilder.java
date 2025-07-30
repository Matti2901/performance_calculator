/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.logging.builder;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.openmrs.logging.MemoryAppender;
import org.openmrs.util.OpenmrsConstants;

import java.io.Serializable;

public class MemoryAppenderBuilder extends AbstractAppender.Builder<MemoryAppenderBuilder> {

	private int bufferSize = 100;

	private StringLayout layout;
	
	public MemoryAppenderBuilder() {
		super();
		setName(OpenmrsConstants.MEMORY_APPENDER_NAME);
	}
	
	public MemoryAppenderBuilder setBufferSize(int bufferSize) {
		if (bufferSize < 0) {
			throw new IllegalArgumentException("bufferSize must be a positive number or 0");
		}

		this.bufferSize = bufferSize;
		return asBuilder();
	}

	@Override
	public Layout<? extends Serializable> getLayout() {
		return layout;
	}

	@Override
	public MemoryAppenderBuilder setLayout(Layout<? extends Serializable> layout) {
		if (layout instanceof StringLayout) {
			return setLayout((StringLayout) layout);
		}

		throw new IllegalArgumentException("MemoryAppender layouts must output string values");
	}

	public MemoryAppenderBuilder setLayout(StringLayout layout) {
		this.layout = layout;
		return asBuilder();
	}

	public MemoryAppender build() {
		return new MemoryAppender(getName(), getFilter(), layout, isIgnoreExceptions(), getPropertyArray(),
			bufferSize);
	}
}
