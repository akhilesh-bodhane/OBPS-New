/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.persistence.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;

/**
 * Compatibility replacement for the legacy org.hibernate.Criteria API, which Hibernate 6 removed. It supports the subset
 * of the API used by this code base (restrictions, aliases/sub-criteria, projections, ordering, paging, root entity
 * de-duplication and alias-based result transformers) and executes it through the JPA Criteria API.
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Criteria implements CriteriaSpecification {

    private static final class AliasDefinition {
        private final String path;
        private final JoinType joinType;

        private AliasDefinition(final String path, final JoinType joinType) {
            this.path = path;
            this.joinType = joinType;
        }
    }

    private static final class State {
        private final Session session;
        private final Class<?> entityClass;
        private final String rootAlias;
        private final List<Criterion> criteria = new ArrayList<>();
        private final Map<String, AliasDefinition> aliases = new LinkedHashMap<>();
        private final List<Order> orders = new ArrayList<>();
        private Projection projection;
        private Integer firstResult;
        private Integer maxResults;
        private ResultTransformer transformer;
        private boolean cacheable;
        private boolean readOnly;
        private org.hibernate.FlushMode flushMode;
        private int subCriteriaCounter;

        private State(final Session session, final Class<?> entityClass, final String rootAlias) {
            this.session = session;
            this.entityClass = entityClass;
            this.rootAlias = rootAlias;
        }
    }

    private final State state;
    /** Alias that unqualified property names are relative to; null for the root criteria. */
    private final String scope;

    private Criteria(final State state, final String scope) {
        this.state = state;
        this.scope = scope;
    }

    public static Criteria create(final Session session, final String entityName) {
        final Class<?> entityClass = session.getMetamodel().getEntities().stream()
                .filter(e -> e.getName().equals(entityName) || e.getJavaType().getName().equals(entityName))
                .map(e -> (Class<?>) e.getJavaType()).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown entity " + entityName));
        return create(session, entityClass);
    }

    public static Criteria create(final Session session, final Class<?> entityClass) {
        return new Criteria(new State(session, entityClass, null), null);
    }

    public static Criteria create(final Session session, final Class<?> entityClass, final String alias) {
        return new Criteria(new State(session, entityClass, alias), null);
    }

    public Criteria add(final Criterion criterion) {
        if (scope == null)
            state.criteria.add(criterion);
        else {
            final String scopeAlias = scope;
            state.criteria.add((cb, r) -> criterion.toPredicate(cb, new ScopedResolver(r, scopeAlias)));
        }
        return this;
    }

    public Criteria addOrder(final Order order) {
        state.orders.add(scope == null ? order : new ScopedOrder(order, scope));
        return this;
    }

    public Criteria createAlias(final String path, final String alias) {
        return createAlias(path, alias, CriteriaSpecification.INNER_JOIN);
    }

    public Criteria createAlias(final String path, final String alias, final int joinType) {
        state.aliases.put(alias, new AliasDefinition(scope == null ? path : scope + "." + path, toJoinType(joinType)));
        return this;
    }

    public Criteria createAlias(final String path, final String alias, final JoinType joinType) {
        state.aliases.put(alias, new AliasDefinition(scope == null ? path : scope + "." + path, joinType));
        return this;
    }

    public Criteria createCriteria(final String path) {
        return createCriteria(path, "_sub" + state.subCriteriaCounter++);
    }

    public Criteria createCriteria(final String path, final String alias) {
        createAlias(path, alias);
        return new Criteria(state, alias);
    }

    public Criteria createCriteria(final String path, final String alias, final int joinType) {
        createAlias(path, alias, joinType);
        return new Criteria(state, alias);
    }

    public Criteria setProjection(final Projection projection) {
        state.projection = projection;
        return this;
    }

    public Criteria setFirstResult(final int firstResult) {
        state.firstResult = firstResult;
        return this;
    }

    public Criteria setMaxResults(final int maxResults) {
        state.maxResults = maxResults;
        return this;
    }

    public Criteria setResultTransformer(final ResultTransformer transformer) {
        state.transformer = transformer;
        return this;
    }

    public Criteria setCacheable(final boolean cacheable) {
        state.cacheable = cacheable;
        return this;
    }

    public Criteria setHibernateFlushMode(final org.hibernate.FlushMode flushMode) {
        return setFlushMode(flushMode);
    }

    public Criteria setFlushMode(final org.hibernate.FlushMode flushMode) {
        state.flushMode = flushMode;
        return this;
    }

    public Criteria setReadOnly(final boolean readOnly) {
        state.readOnly = readOnly;
        return this;
    }

    public Criteria setFetchMode(final String path, final FetchMode fetchMode) {
        return this;
    }

    public Criteria setComment(final String comment) {
        return this;
    }

    public Criteria setCacheRegion(final String region) {
        return this;
    }

    /**
     * Builds this criteria as a JPA subquery of the given parent query (used by {@link Subqueries}).
     */
    Subquery<Object> toSubquery(final CriteriaBuilder cb, final AbstractQuery<?> parent) {
        final Subquery<Object> subquery = parent.subquery(Object.class);
        final Root<?> root = subquery.from(state.entityClass);
        final Resolver resolver = new Resolver(root, state);
        resolver.query = subquery;
        if (state.projection != null) {
            final List<Selection<?>> selections = state.projection.selections(cb, resolver);
            subquery.select((jakarta.persistence.criteria.Expression) selections.get(0));
            subquery.distinct(state.projection.isDistinct());
        } else
            subquery.select((jakarta.persistence.criteria.Expression) root);
        if (!state.criteria.isEmpty()) {
            final List<Predicate> predicates = new ArrayList<>();
            for (final Criterion criterion : state.criteria)
                predicates.add(criterion.toPredicate(cb, resolver));
            subquery.where(predicates.toArray(new Predicate[0]));
        }
        return subquery;
    }

    static Criteria detached(final Class<?> entityClass, final String alias) {
        return new Criteria(new State(null, entityClass, alias), null);
    }

    public Criteria with(final Session session) {
        final State copy = new State(session, state.entityClass, state.rootAlias);
        copy.criteria.addAll(state.criteria);
        copy.aliases.putAll(state.aliases);
        copy.orders.addAll(state.orders);
        copy.projection = state.projection;
        copy.transformer = state.transformer;
        return new Criteria(copy, null);
    }

    public List list() {
        final CriteriaBuilder cb = state.session.getCriteriaBuilder();
        final CriteriaQuery<Object> query = cb.createQuery(Object.class);
        final Root<?> root = query.from(state.entityClass);
        final Resolver resolver = new Resolver(root, state);
        resolver.query = query;
        final boolean rootEntityTransform = state.transformer == CriteriaSpecification.DISTINCT_ROOT_ENTITY;

        if (state.projection != null) {
            final List<Selection<?>> selections = state.projection.selections(cb, resolver);
            if (selections.size() == 1)
                query.select((Selection) selections.get(0));
            else
                query.multiselect(selections);
            query.distinct(state.projection.isDistinct());
            final List<jakarta.persistence.criteria.Expression<?>> groupings = state.projection.groupings(resolver);
            if (!groupings.isEmpty())
                query.groupBy(groupings);
        } else
            query.select((Selection) root);

        if (!state.criteria.isEmpty()) {
            final List<Predicate> predicates = new ArrayList<>();
            for (final Criterion criterion : state.criteria)
                predicates.add(criterion.toPredicate(cb, resolver));
            query.where(predicates.toArray(new Predicate[0]));
        }
        if (!state.orders.isEmpty()) {
            final List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
            for (final Order order : state.orders)
                orders.add(order.toJpaOrder(cb, resolver));
            query.orderBy(orders);
        }

        final Query<Object> typedQuery = state.session.createQuery(query);
        if (state.firstResult != null)
            typedQuery.setFirstResult(state.firstResult);
        if (state.maxResults != null)
            typedQuery.setMaxResults(state.maxResults);
        typedQuery.setCacheable(state.cacheable);
        if (state.readOnly)
            typedQuery.setReadOnly(true);
        if (state.flushMode != null)
            typedQuery.setHibernateFlushMode(state.flushMode);

        List result = typedQuery.getResultList();
        if (rootEntityTransform)
            return new ArrayList<>(new LinkedHashSet<>(result));
        if (state.transformer != null && state.projection != null) {
            final String[] aliases = state.projection.aliases().toArray(new String[0]);
            final List transformed = new ArrayList<>(result.size());
            for (final Object row : result)
                transformed.add(state.transformer.transformTuple(row instanceof Object[] ? (Object[]) row
                        : new Object[] { row }, aliases));
            result = state.transformer.transformList(transformed);
        }
        return result;
    }

    public Object uniqueResult() {
        final List results = list();
        if (results.isEmpty())
            return null;
        final Set<Object> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
        distinct.addAll(results);
        if (distinct.size() > 1)
            throw new NonUniqueResultException("result returns more than one element");
        return results.get(0);
    }

    private static JoinType toJoinType(final int joinType) {
        return joinType == CriteriaSpecification.INNER_JOIN ? JoinType.INNER : JoinType.LEFT;
    }

    private static final class ScopedResolver implements PathResolver {
        private final PathResolver delegate;
        private final String scope;

        private ScopedResolver(final PathResolver delegate, final String scope) {
            this.delegate = delegate;
            this.scope = scope;
        }

        @Override
        public Path path(final String property) {
            final int dot = property.indexOf('.');
            // a dotted path that starts with a known alias is absolute, anything else is relative to the scope alias
            if (dot > 0 && ((Resolver) unwrap(delegate)).isAlias(property.substring(0, dot)))
                return delegate.path(property);
            return delegate.path(scope + "." + property);
        }

        @Override
        public AbstractQuery<?> query() {
            return delegate.query();
        }

        @Override
        public Root<?> root() {
            return delegate.root();
        }

        @Override
        public String idName() {
            return delegate.idName();
        }

        private static PathResolver unwrap(final PathResolver resolver) {
            return resolver instanceof ScopedResolver ? unwrap(((ScopedResolver) resolver).delegate) : resolver;
        }
    }

    private static final class ScopedOrder extends Order {
        private final Order order;
        private final String scope;

        private ScopedOrder(final Order order, final String scope) {
            super(order.getPropertyName(), order.isAscending());
            this.order = order;
            this.scope = scope;
        }

        @Override
        public jakarta.persistence.criteria.Order toJpaOrder(final CriteriaBuilder cb, final PathResolver resolver) {
            return order.toJpaOrder(cb, new ScopedResolver(resolver, scope));
        }
    }

    private static final class Resolver implements PathResolver {
        private final Root<?> root;
        private final State state;
        private final Map<String, From<?, ?>> joins = new LinkedHashMap<>();
        private AbstractQuery<?> query;

        private Resolver(final Root<?> root, final State state) {
            this.root = root;
            this.state = state;
            for (final Map.Entry<String, AliasDefinition> alias : state.aliases.entrySet()) {
                final String[] parts = alias.getValue().path.split("\\.");
                From<?, ?> base = root;
                int index = 0;
                if (joins.containsKey(parts[0]) && parts.length > 1) {
                    base = joins.get(parts[0]);
                    index = 1;
                } else if (parts[0].equals(state.rootAlias) && parts.length > 1)
                    index = 1;
                for (; index < parts.length; index++)
                    base = base.join(parts[index], index == parts.length - 1 ? alias.getValue().joinType : JoinType.INNER);
                joins.put(alias.getKey(), base);
            }
        }

        private boolean isAlias(final String name) {
            return joins.containsKey(name) || name.equals(state.rootAlias);
        }

        @Override
        public Path path(final String property) {
            final String[] parts = property.split("\\.");
            Path<?> path = root;
            int index = 0;
            if (parts.length > 1 && joins.containsKey(parts[0])) {
                path = joins.get(parts[0]);
                index = 1;
            } else if (parts.length > 1 && parts[0].equals(state.rootAlias))
                index = 1;
            for (; index < parts.length; index++)
                path = path.get(parts[index]);
            return path;
        }

        @Override
        public AbstractQuery<?> query() {
            return query;
        }

        @Override
        public Root<?> root() {
            return root;
        }

        @Override
        public String idName() {
            final jakarta.persistence.metamodel.EntityType<?> entityType = root.getModel();
            return entityType.getId(entityType.getIdType().getJavaType()).getName();
        }
    }
}
