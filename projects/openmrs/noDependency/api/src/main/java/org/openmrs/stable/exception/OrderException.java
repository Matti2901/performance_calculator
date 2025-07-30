/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.stable.exception;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

public class OrderException extends RuntimeException {
	
	public OrderException(String message) {
		super(message);
	}
	
	public OrderException(String messageKey, Object[] parameters) {
		super(Context.getMessageSourceService().getMessage(messageKey, parameters, Context.getLocale()));
	}
	
	public OrderException(String messageKey, Object[] parameters, Throwable cause) {
		super(Context.getMessageSourceService().getMessage(messageKey, parameters, Context.getLocale()), cause);
	}
}
