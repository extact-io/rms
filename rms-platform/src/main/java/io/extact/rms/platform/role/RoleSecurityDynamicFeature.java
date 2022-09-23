package io.extact.rms.platform.role;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;

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
