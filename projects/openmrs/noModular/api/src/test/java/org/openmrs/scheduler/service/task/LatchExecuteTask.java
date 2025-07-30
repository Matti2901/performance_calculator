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

/**
 * task that waits in its execute method until all other tasks on the same latch have called
 * execute()
 */
public class LatchExecuteTask extends SchedulerServiceTest.LatchTask {

	@Override
	public void initialize(TaskDefinition config) {
		super.initialize(config);
	}

	@Override
	public void execute() {
		waitForLatch();
	}
}
