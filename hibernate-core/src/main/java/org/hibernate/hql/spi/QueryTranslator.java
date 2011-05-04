/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.hql.spi;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.type.Type;

/**
 * Defines the contract of an HQL->SQL translator.
 *
 * @author josh
 */
public interface QueryTranslator {

	// Error message constants.
	public static final String ERROR_CANNOT_FETCH_WITH_ITERATE = "fetch may not be used with scroll() or iterate()";
	public static final String ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR = "Named parameter does not appear in Query: ";
    public static final String ERROR_CANNOT_DETERMINE_TYPE = "Could not determine type of: ";
	public static final String ERROR_CANNOT_FORMAT_LITERAL =  "Could not format constant value to SQL literal: ";

	/**
	 * Compile a "normal" query. This method may be called multiple
	 * times. Subsequent invocations are no-ops.
	 *
	 * @param replacements Defined query substitutions.
	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
	 * @throws QueryException   There was a problem parsing the query string.
	 * @throws MappingException There was a problem querying defined mappings.
	 */
	void compile(Map replacements, boolean shallow) throws QueryException, MappingException;

	/**
	 * Perform a list operation given the underlying query definition.
	 *
	 * @param session         The session owning this query.
	 * @param queryParameters The query bind parameters.
	 * @return The query list results.
	 * @throws HibernateException
	 */
	List list(SessionImplementor session, QueryParameters queryParameters)
			throws HibernateException;

	/**
	 * Perform an iterate operation given the underlying query definition.
	 *
	 * @param queryParameters The query bind parameters.
	 * @param session         The session owning this query.
	 * @return An iterator over the query results.
	 * @throws HibernateException
	 */
	Iterator iterate(QueryParameters queryParameters, EventSource session)
			throws HibernateException;

	/**
	 * Perform a scroll operation given the underlying query definition.
	 *
	 * @param queryParameters The query bind parameters.
	 * @param session         The session owning this query.
	 * @return The ScrollableResults wrapper around the query results.
	 * @throws HibernateException
	 */
	ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
			throws HibernateException;

	/**
	 * Perform a bulk update/delete operation given the underlying query definition.
	 *
	 * @param queryParameters The query bind parameters.
	 * @param session         The session owning this query.
	 * @return The number of entities updated or deleted.
	 * @throws HibernateException
	 */
	int executeUpdate(QueryParameters queryParameters, SessionImplementor session)
			throws HibernateException;

	/**
	 * Returns the set of query spaces (table names) that the query refers to.
	 *
	 * @return A set of query spaces (table names).
	 */
	Set getQuerySpaces();

	/**
	 * Retrieve the query identifier for this translator.  The query identifier is
	 * used in states collection.
	 *
	 * @return the identifier
	 */
	String getQueryIdentifier();

	/**
	 * Returns the SQL string generated by the translator.
	 *
	 * @return the SQL string generated by the translator.
	 */
	String getSQLString();

	List collectSqlStrings();

	/**
	 * Returns the HQL string processed by the translator.
	 *
	 * @return the HQL string processed by the translator.
	 */
	String getQueryString();

	/**
	 * Returns the filters enabled for this query translator.
	 *
	 * @return Filters enabled for this query execution.
	 */
	Map getEnabledFilters();

	/**
	 * Returns an array of Types represented in the query result.
	 *
	 * @return Query return types.
	 */
	Type[] getReturnTypes();
	
	/**
	 * Returns an array of HQL aliases
	 */
	String[] getReturnAliases();

	/**
	 * Returns the column names in the generated SQL.
	 *
	 * @return the column names in the generated SQL.
	 */
	String[][] getColumnNames();

	/**
	 * Return information about any parameters encountered during
	 * translation.
	 *
	 * @return The parameter information.
	 */
	ParameterTranslations getParameterTranslations();

	/**
	 * Validate the scrollability of the translated query.
	 *
	 * @throws HibernateException
	 */
	void validateScrollability() throws HibernateException;

	/**
	 * Does the translated query contain collection fetches?
	 *
	 * @return true if the query does contain collection fetched;
	 * false otherwise.
	 */
	boolean containsCollectionFetches();

	boolean isManipulationStatement();

	public Class getDynamicInstantiationResultType();
}