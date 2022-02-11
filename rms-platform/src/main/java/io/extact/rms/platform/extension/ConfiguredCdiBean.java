package io.extact.rms.platform.extension;

import java.lang.annotation.Annotation;

public class ConfiguredCdiBean {

    Class<?> beanClass;
    Annotation scope;
    String id;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Annotation getScoped() {
        return scope;
    }

    public String getId() {
        return id;
    }
}