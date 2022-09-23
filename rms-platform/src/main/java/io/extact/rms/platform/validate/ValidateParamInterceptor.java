package io.extact.rms.platform.validate;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.groups.Default;
import jakarta.validation.metadata.BeanDescriptor;

import org.apache.commons.lang3.ArrayUtils;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@ValidateParam
public class ValidateParamInterceptor {

    @Inject
    @InnerValidator
    private Validator validator;

    @AroundInvoke
    public Object obj(InvocationContext ic) throws Exception {

        // メソッド定義を優先してバリデーショングループの指定を取得
        var methodValidateGroup = ic.getMethod().getAnnotation(ValidateGroup.class);
        var classValidateGroup = ic.getTarget().getClass().getAnnotation(ValidateGroup.class);
        Class<?>[] groups;
        if (methodValidateGroup != null) {
            groups = methodValidateGroup.groups();
        } else if (classValidateGroup != null){
            groups = classValidateGroup.groups();
        } else {
            groups = null;
        }

        // バリデーショングループにDefaultグループをデフォルトで追加してあげる
        groups = (ArrayUtils.contains(groups, Default.class))
                ? groups
                : ArrayUtils.add(groups, Default.class);

        // Bean Validationのパラメータバリデーションの実行
        var executableValidator = validator.forExecutables();
        Set<ConstraintViolation<Object>> result = executableValidator.validateParameters(
                ic.getTarget(), ic.getMethod(), ic.getParameters(), groups);

        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }

        return ic.proceed();
    }


    // ----------------------------------------------------- inner classes

    /**
     * CDIでValidatorをラップしたクラス。
     * 以下の理由からCDI-BeanValidatorインテグレーション機能は使わず独自実装を行い、
     * CDI-BeanValidatorインテグレーション機能がデフォルトで有効になっている環境でも取得可能なように
     * 限定氏を付けている。
     * <ul>
     * <li>JavaEE の"Method validation"は広範に効きすぎなのでOFFにしたい
     * <li>@InjectでValidatorインスタンスを取得するにはHibernateのValidatorExtensionを有効にする必要が
     *     あるがJerseyとのvalidate機能も混在してライブラリと機能実態がカオスになる
     * <li>Validatorインスタンスの@Injectによる取得はclassパス上にhibernate-validator-cdi.jarなどがあるか
     *     によりポータビリティに難がある
     * </ul>
     */
    @ApplicationScoped
    @InnerValidator
    public static class InnerValidatorImpl implements Validator {

        private Validator delegate;

        @PostConstruct
        public void init() {
            Configuration<?> config = Validation.byDefaultProvider().configure();
            var factory = config.buildValidatorFactory();
            delegate = factory.getValidator();
            factory.close();
        }

        public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
            return delegate.validate(object, groups);
        }

        public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
            return delegate.validateProperty(object, propertyName, groups);
        }

        public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
            return delegate.validateValue(beanType, propertyName, value, groups);
        }

        public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
            return delegate.getConstraintsForClass(clazz);
        }

        public <T> T unwrap(Class<T> type) {
            return delegate.unwrap(type);
        }

        public ExecutableValidator forExecutables() {
            return delegate.forExecutables();
        }
    }

    @Qualifier
    @Retention(RUNTIME)
    @Target({ TYPE, METHOD, FIELD, CONSTRUCTOR })
    public @interface InnerValidator {
    }

}
