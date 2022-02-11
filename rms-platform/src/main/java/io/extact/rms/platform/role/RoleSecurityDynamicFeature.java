package io.extact.rms.platform.role;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.eclipse.microprofile.config.ConfigProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleSecurityDynamicFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {

        if (!enableFilter()) {
            log.debug("設定(jwt.filter.enable)がOFFになっているためロールチェックは行いません");
            return;
        }

        // methodのアノテーションから優先して確認
        var method = resourceInfo.getResourceMethod();
        if (method.getAnnotation(DenyAll.class) != null) {
            context.register(new DenyAllRequestFilter());
            return;
        }
        var  rolesAllowed = method.getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null) {
            context.register(new RolesAllowedRequestFilter(rolesAllowed));
            return;
        }

        // classのアノテーションを確認
        var clazz = resourceInfo.getResourceClass();
        if (clazz.getAnnotation(DenyAll.class) != null) {
            context.register(new DenyAllRequestFilter());
            return;
        }
        rolesAllowed = clazz.getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null) {
            context.register(new RolesAllowedRequestFilter(rolesAllowed));
            return;
        }
        // 上に該当しないDenyAllもRolesAllowedも付いてないメソッドはどのロールでもアクセス可能
    }

    private boolean enableFilter() {
        var config = ConfigProvider.getConfig();
        return config.getValue("jwt.filter.enable", Boolean.class);
    }
}
