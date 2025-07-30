/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.notification.db;

import java.util.List;

import org.openmrs.User;
import org.openmrs.api.db.dao.context.DAOException;
import org.openmrs.notification.alert.Alert;
import org.openmrs.notification.alert.AlertService;

/**
 * Database methods for the AlertService
 * 
 * @see org.openmrs.api.context.Context
 * @see AlertService
 */
public interface AlertDAO {
	
	/**
	 * @see AlertService#saveAlert(Alert)
	 */
	public Alert saveAlert(Alert alert) throws DAOException;
	
	/**
	 * @see AlertService#getAlert(Integer)
	 */
	public Alert getAlert(Integer alertId) throws DAOException;
	
	/**
	 * @see AlertService#getAlerts(org.openmrs.User, boolean, boolean)
	 */
	public List<Alert> getAlerts(User user, boolean includeRead, boolean includeVoided) throws DAOException;
	
	/**
	 * @see AlertService#purgeAlert(Alert)
	 */
	public void deleteAlert(Alert alert) throws DAOException;
	
	/**
	 * @see AlertService#getAllAlerts(boolean)
	 */
	public List<Alert> getAllAlerts(boolean includeExpired);
	
}
