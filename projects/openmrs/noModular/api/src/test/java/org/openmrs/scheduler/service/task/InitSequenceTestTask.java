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

import org.openmrs.scheduler.engine.TaskDefinition;
import org.openmrs.scheduler.service.SchedulerServiceTest;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * task that checks for its execute method running at the same time as its initialize method
 */
public class InitSequenceTestTask extends AbstractTask {

	@Override
	public void initialize(TaskDefinition config) {

		super.initialize(config);

		// wait for any other thread to run the execute method
		try {
			Thread.sleep(700);
		} catch (InterruptedException ignored) {
		}

		// set to false if execute() method was running concurrently and has cleared the latch
		SchedulerServiceTest.consecutiveInitResult.set(SchedulerServiceTest.latch.getCount() != 0);
	}

	@Override
	public void execute() {
		// clear the latch to signal the main thread
		SchedulerServiceTest.latch.countDown();
	}
}
