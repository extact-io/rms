package io.extact.rms.application.service;

import java.util.List;

import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.persistence.GenericRepository;

public interface GenericService<T> {

    default T get(int id) {
        return getRepository().get(id);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }

    T add(T entity);

    default T update(T entity) {
        var updated = getRepository().update(entity);
        if (updated == null) {
            throw new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND);
        }
        return getRepository().update(entity);
    }

    default void delete(int id) {
        var target = getRepository().get(id);
        if (target == null) {
            throw new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND);
        }
        getRepository().delete(target);
    }

    GenericRepository<T> getRepository();
}
