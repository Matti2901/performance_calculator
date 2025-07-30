/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web.filter.initialization.executor;

import liquibase.changelog.ChangeSet;
import org.openmrs.liquibase.ChangeSetExecutorCallback;
import org.openmrs.web.filter.initialization.InitializationFilter;

public class PrintingChangeSetExecutorCallback implements ChangeSetExecutorCallback {

	private final InitializationFilter.InitializationCompletion initializationCompletion;
	public int i = 1;

	public String message;

	public PrintingChangeSetExecutorCallback(InitializationFilter.InitializationCompletion initializationCompletion, String message) {
		this.initializationCompletion = initializationCompletion;
		this.message = message;
	}

	/**
	 * @see ChangeSetExecutorCallback#executing(ChangeSet, int)
	 */
	@Override
	public void executing(ChangeSet changeSet, int numChangeSetsToRun) {
		initializationCompletion.setMessage(message + " (" + i++ + "/" + numChangeSetsToRun + "): Author: "
			+ changeSet.getAuthor() + " Comments: " + changeSet.getComments() + " Description: "
			+ changeSet.getDescription());
		float numChangeSetsToRunFloat = (float) numChangeSetsToRun;
		float j = (float) i;
		initializationCompletion.setCompletedPercentage(Math.round(j * 100 / numChangeSetsToRunFloat));
	}

}
