package io.extact.rms.application.persistence.file.converter;

import io.extact.rms.application.exception.RmsSystemException;

public interface EntityArrayConverter<T> {

    T toEntity(String[] attributes) throws RmsSystemException;

    String[] toArray(T entity);
}
