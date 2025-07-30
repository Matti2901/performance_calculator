/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.logic.result.data;

/**
 * Core datatypes for a result. Each result is one of these datatypes, but can be easily coerced
 * into the other datatypes. To promote flexibility and maximize re-usability of logic rules,
 * the value of a result can be controlled individually for each datatype &mdash; i.e., specific
 * datatype representations of a single result can be overridden. For example, a result could
 * have a <em>numeric</em> value of 0.15 and its text value could be overridden to be
 * "15 percent" or "Fifteen percent."
 */
public enum Datatype {
	/**
	 * Represents a true/false type of result
	 */
	BOOLEAN,
	/**
	 * Represents a Concept type of result
	 */
	CODED,
	/**
	 * Represents a date type of result
	 */
	DATETIME,
	/**
	 * Represents number (float, double, int) type of results
	 */
	NUMERIC,
	/**
	 * Represents string type of results
	 */
	TEXT
}
