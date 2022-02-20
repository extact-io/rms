package io.extact.rms.application.service;

import java.util.List;
import java.util.function.Consumer;

import io.extact.rms.application.domain.IdAccessable;
import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.persistence.GenericRepository;

public interface GenericService<T extends IdAccessable> {

    default T get(int id) {
        return getRepository().get(id);
    }

    default List<T> findAll() {
        return getRepository().findAll();
    }

    default T add(T entity) {
        if (getDuplicateChecker() != null) {
            getDuplicateChecker().accept(entity);
        }
        getRepository().add(entity);
        return get(entity.getId());
    }

    default T update(T entity) {
        if (getRepository().get(entity.getId()) == null) {
            throw new BusinessFlowException("target does not exist for id", CauseType.NOT_FOUND);
        }
        if (getDuplicateChecker() != null) {
            getDuplicateChecker().accept(entity);
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

    default Consumer<T> getDuplicateChecker() {
        return null;
    }

    GenericRepository<T> getRepository();
}
