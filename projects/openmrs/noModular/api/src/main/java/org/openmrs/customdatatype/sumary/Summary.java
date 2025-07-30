/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.customdatatype.sumary;

/**
 * A short representation of a custom value, along with an indication of whether this is the complete value,
 * or just a summary.
 */
public class Summary {

	private String summary;

	private boolean complete;

	/**
	 * @param summary
	 * @param complete
	 */
	public Summary(String summary, boolean complete) {
		this.summary = summary;
		this.complete = complete;
	}

	/**
	 * @return the short representation of a custom value
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return if true, then getSummary() returns a complete view of the custom value; otherwise the value is
	 * in fact a summary
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * @param complete the complete to set
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return summary;
	}
}
