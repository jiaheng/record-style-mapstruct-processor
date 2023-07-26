package jh.recordmapper.spi;

import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

import java.util.Set;
import java.util.logging.Logger;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

public class CustomAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    private static final Logger LOG =
            Logger.getLogger(CustomAccessorNamingStrategy.class.getName());

    private static final Set<String> EXCLUDED_GETTER_KEYWORD = Set.of("toString", "hashCode");

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        return super.isGetterMethod(method) || isRecordStyleGetterName(method);
    }

    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {
        if (isRecordStyleGetterName(getterOrSetterMethod)) {
            String methodName = getterOrSetterMethod.getSimpleName().toString();
            LOG.finest(
                    () ->
                            "mapping getter "
                                    + methodName
                                    + "() to "
                                    + IntrospectorUtils.decapitalize(methodName)
                                    + "() with return type: "
                                    + getterOrSetterMethod.getReturnType().toString());
            return IntrospectorUtils.decapitalize(methodName);
        }
        return super.getPropertyName(getterOrSetterMethod);
    }

    private boolean isRecordStyleGetterName(ExecutableElement method) {
        boolean isDefaultGetterMethod = super.isGetterMethod(method);
        if (isDefaultGetterMethod) {
            return false;
        }

        if (!method.getParameters().isEmpty()
                || method.getReturnType().getKind() == TypeKind.VOID) {
            return false;
        }

        String methodName = method.getSimpleName().toString();
        return !EXCLUDED_GETTER_KEYWORD.contains(methodName);
    }
}
