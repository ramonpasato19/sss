package com.powerfin.helper;

import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JPAHelper {

	public static <T> T getSingleResult(TypedQuery<T> query) {
		query.setMaxResults(1);
		List<T> list = query.getResultList();
		if (list == null || list.isEmpty()) {
			return null;
		}

		return list.get(0);
	}

	@SuppressWarnings("rawtypes")
	public static Object getSingleResult(Query query) {
		List results = query.getResultList();
		if (results.isEmpty())
			return null;
		else if (results.size() == 1)
			return results.get(0);
		throw new NonUniqueResultException();
	}
}
