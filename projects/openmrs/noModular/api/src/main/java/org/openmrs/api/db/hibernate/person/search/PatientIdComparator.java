/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db.hibernate.person.search;

import org.openmrs.Patient;

import java.util.Comparator;
import java.util.Map;

public class PatientIdComparator implements Comparator<Patient> {

	private Map<Integer, Integer> sortOrder;

	public PatientIdComparator(Map<Integer, Integer> sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(Patient patient1, Patient patient2) {
		Integer patPos1 = sortOrder.get(patient1.getPatientId());
		if (patPos1 == null) {
			throw new IllegalArgumentException("Bad patient encountered: " + patient1.getPatientId());
		}
		Integer patPos2 = sortOrder.get(patient2.getPatientId());
		if (patPos2 == null) {
			throw new IllegalArgumentException("Bad patient encountered: " + patient2.getPatientId());
		}
		return patPos1.compareTo(patPos2);
	}
}
