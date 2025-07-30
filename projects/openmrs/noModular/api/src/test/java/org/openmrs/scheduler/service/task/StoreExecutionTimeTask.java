/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.scheduler.service.task;

import org.openmrs.scheduler.service.SchedulerServiceTest;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Just stores the execution time.
 */
public class StoreExecutionTimeTask extends AbstractTask {

	@Override
	public void execute() {
		SchedulerServiceTest.actualExecutionTime = System.currentTimeMillis();
		// signal the test method that the task has executed
		SchedulerServiceTest.latch.countDown();
	}
}
