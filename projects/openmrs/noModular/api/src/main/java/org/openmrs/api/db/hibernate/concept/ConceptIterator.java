/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db.hibernate.concept;

import org.openmrs.Concept;

import java.util.Iterator;

/**
 * An iterator that loops over all concepts in the dictionary one at a time
 */
public class ConceptIterator implements Iterator<Concept> {

	private final HibernateConceptDAO hibernateConceptDAO;
	public Concept currentConcept = null;

	public Concept nextConcept;

	public ConceptIterator(HibernateConceptDAO hibernateConceptDAO) {
		this.hibernateConceptDAO = hibernateConceptDAO;
		final int firstConceptId = hibernateConceptDAO.getMinConceptId();
		nextConcept = hibernateConceptDAO.getConcept(firstConceptId);
	}

	/**
	 * @see Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return nextConcept != null;
	}

	/**
	 * @see Iterator#next()
	 */
	@Override
	public Concept next() {
		if (currentConcept != null) {
			hibernateConceptDAO.sessionFactory.getCurrentSession().evict(currentConcept);
		}

		currentConcept = nextConcept;
		nextConcept = hibernateConceptDAO.getNextConcept(currentConcept);

		return currentConcept;
	}

	/**
	 * @see Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
