/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web.filter.update.completation;

import liquibase.changelog.ChangeSet;
import org.apache.commons.collections.CollectionUtils;
import org.openmrs.liquibase.ChangeLogDetective;
import org.openmrs.liquibase.ChangeLogVersionFinder;
import org.openmrs.liquibase.ChangeSetExecutorCallback;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.DatabaseUpdater;
import org.openmrs.util.DatabaseUpdaterLiquibaseProvider;
import org.openmrs.util.InputRequiredException;
import org.openmrs.web.filter.update.UpdateFilter;
import org.openmrs.web.filter.util.ErrorMessageConstants;

import java.util.*;

/**
 * This class controls the final steps and is used by the ajax calls to know what updates have been
 * executed. TODO: Break this out into a separate (non-inner) class
 */
public class UpdateFilterCompletion {

	private final UpdateFilter updateFilter;
	private Thread thread;

	private String executingChangesetId = null;

	private List<String> changesetIds = new ArrayList<>();

	private Map<String, Object[]> errors = new HashMap<>();

	private String message = null;

	private boolean erroneous = false;

	public boolean hasUpdateWarnings = false;

	private List<String> updateWarnings = new LinkedList<>();

	public synchronized void reportError(String error, Object... params) {
		Map<String, Object[]> reportedErrors = new HashMap<>();
		reportedErrors.put(error, params);
		reportErrors(reportedErrors);
	}

	public synchronized void reportErrors(Map<String, Object[]> errs) {
		errors.putAll(errs);
		erroneous = true;
	}

	public synchronized boolean hasErrors() {
		return erroneous;
	}

	public synchronized Map<String, Object[]> getErrors() {
		return errors;
	}

	/**
	 * Start the completion stage. This fires up the thread to do all the work.
	 */
	public void start() {
		UpdateFilter.setUpdatesRequired(true);
		thread.start();
	}

	public synchronized void setMessage(String message) {
		this.message = message;
	}

	public synchronized String getMessage() {
		return message;
	}

	public synchronized void addChangesetId(String changesetid) {
		this.changesetIds.add(changesetid);
		this.executingChangesetId = changesetid;
	}

	public synchronized List<String> getChangesetIds() {
		return changesetIds;
	}

	public synchronized String getExecutingChangesetId() {
		return executingChangesetId;
	}

	/**
	 * @return the database updater Warnings
	 */
	public synchronized List<String> getUpdateWarnings() {
		return updateWarnings;
	}

	public synchronized boolean hasWarnings() {
		return hasUpdateWarnings;
	}

	public synchronized void reportWarnings(List<String> warnings) {
		updateWarnings.addAll(warnings);
		hasUpdateWarnings = true;
	}

	/**
	 * This class does all the work of creating the desired database, user, updates, etc
	 */
	public UpdateFilterCompletion(UpdateFilter updateFilter) {
		this.updateFilter = updateFilter;
		Runnable r = new Runnable() {

			/**
			 * TODO split this up into multiple testable methods
			 *
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				try {
					/**
					 * A callback class that prints out info about liquibase changesets
					 */
					class PrintingChangeSetExecutorCallback implements ChangeSetExecutorCallback {

						private String message;

						public PrintingChangeSetExecutorCallback(String message) {
							this.message = message;
						}

						/**
						 * @see ChangeSetExecutorCallback#executing(liquibase.changelog.ChangeSet, int)
						 */
						@Override
						public void executing(ChangeSet changeSet, int numChangeSetsToRun) {
							addChangesetId(changeSet.getId());
							setMessage(message);
						}

					}

					try {
						setMessage("Updating the database to the latest version");

						ChangeLogDetective changeLogDetective = new ChangeLogDetective();
						ChangeLogVersionFinder changeLogVersionFinder = new ChangeLogVersionFinder();

						List<String> changelogs = new ArrayList<>();
						List<String> warnings = new ArrayList<>();

						String version = changeLogDetective.getInitialLiquibaseSnapshotVersion(DatabaseUpdater.CONTEXT,
							new DatabaseUpdaterLiquibaseProvider());

						updateFilter.log.debug(
							"updating the database with versions of liquibase-update-to-latest files greater than '{}'",
							version);

						changelogs.addAll(changeLogVersionFinder
							.getUpdateFileNames(changeLogVersionFinder.getUpdateVersionsGreaterThan(version)));

						updateFilter.log.debug("found applicable Liquibase update change logs: {}", changelogs);

						for (String changelog : changelogs) {
							updateFilter.log.debug("applying Liquibase changelog '{}'", changelog);

							List<String> currentWarnings = DatabaseUpdater.executeChangelog(changelog,
								new PrintingChangeSetExecutorCallback("executing Liquibase changelog :" + changelog));

							if (currentWarnings != null) {
								warnings.addAll(currentWarnings);
							}
						}
						executingChangesetId = null; // clear out the last changeset

						if (CollectionUtils.isNotEmpty(warnings)) {
							reportWarnings(warnings);
						}
					} catch (InputRequiredException inputRequired) {
						// the user would be stepped through the questions returned here.
						updateFilter.log.error("Not implemented", inputRequired);
						updateFilter.updateFilterModel.updateChanges();
						reportError(ErrorMessageConstants.UPDATE_ERROR_INPUT_NOT_IMPLEMENTED,
							inputRequired.getMessage());
						return;
					} catch (DatabaseUpdateException e) {
						updateFilter.log.error("Unable to update the database", e);
						Map<String, Object[]> databaseUpdateErrors = new HashMap<>();
						databaseUpdateErrors.put(ErrorMessageConstants.UPDATE_ERROR_UNABLE, null);
						for (String errorMessage : Arrays.asList(e.getMessage().split("\n"))) {
							databaseUpdateErrors.put(errorMessage, null);
						}
						updateFilter.updateFilterModel.updateChanges();
						reportErrors(databaseUpdateErrors);
						return;
					} catch (Exception e) {
						updateFilter.log.error("Unable to update the database", e);
						return;
					}

					setMessage("Starting OpenMRS");
					try {
						updateFilter.startOpenmrs(updateFilter.filterConfig.getServletContext());
					} catch (Exception e) {
						updateFilter.log.error("Unable to complete the startup.", e);
						reportError(ErrorMessageConstants.UPDATE_ERROR_COMPLETE_STARTUP, e.getMessage());
						return;
					}

					// set this so that the wizard isn't run again on next page load
					UpdateFilter.setUpdatesRequired(false);
				} finally {
					if (!hasErrors()) {
						UpdateFilter.setUpdatesRequired(false);
					}
					//reset to let other user's make requests after updates are run
					UpdateFilter.isDatabaseUpdateInProgress = false;
				}
			}
		};

		thread = new Thread(r);
	}
}
