/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import java.sql.Timestamp;
import java.util.Date;

public class Comparator {
	
	/**
	 * Compares two java.util.Date objects, but handles java.sql.Timestamp (which is not directly
	 * comparable to a date) by dropping its nanosecond value.
	 */
	public static int compare(Date d1, Date d2) {
		if (d1 instanceof Timestamp && d2 instanceof Timestamp) {
			return d1.compareTo(d2);
		}
		if (d1 instanceof Timestamp) {
			d1 = new Date(d1.getTime());
		}
		if (d2 instanceof Timestamp) {
			d2 = new Date(d2.getTime());
		}
		return d1.compareTo(d2);
	}

	public static <Arg1, Arg2 extends Arg1> boolean nullSafeEquals(Arg1 d1, Arg2 d2) {
		if (d1 == null) {
			return d2 == null;
		} else if (d2 == null) {
			return false;
		}
		return (d1 instanceof Date && d2 instanceof Date) ? compare((Date) d1, (Date) d2) == 0 : d1.equals(d2);
	}
}
