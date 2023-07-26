package jh.recordmapper.spi;

import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

import java.util.logging.Logger;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

public class CustomAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

    private static final Logger LOG =
            Logger.getLogger(CustomAccessorNamingStrategy.class.getName());

    @Override
    public boolean isGetterMethod(ExecutableElement method) {
        return method.getParameters().isEmpty();
    }

    @Override
    public String getPropertyName(ExecutableElement getterOrSetterMethod) {
        if (isRecordStyleGetterName(getterOrSetterMethod)) {
            String methodName = getterOrSetterMethod.getSimpleName().toString();
            LOG.info("mapping " + methodName + " to " + IntrospectorUtils.decapitalize(methodName));
            return IntrospectorUtils.decapitalize(methodName);
        }
        return super.getPropertyName(getterOrSetterMethod);
        //        String methodName = getterOrSetterMethod.getSimpleName().toString();
        //        return Introspector.decapitalize(methodName);
    }

    private boolean isRecordStyleGetterName(ExecutableElement method) {
        if (!method.getParameters().isEmpty()) {
            // If the method has parameters it can't be a getter
            return false;
        }
        String methodName = method.getSimpleName().toString();

        boolean isNonBooleanGetterName =
                methodName.startsWith("get")
                        && methodName.length() > 3
                        && method.getReturnType().getKind() != TypeKind.VOID;

        boolean isBooleanGetterName = methodName.startsWith("is") && methodName.length() > 2;
        boolean returnTypeIsBoolean =
                method.getReturnType().getKind() == TypeKind.BOOLEAN
                        || "java.lang.Boolean".equals(getQualifiedName(method.getReturnType()));

        return !isNonBooleanGetterName && !(isBooleanGetterName && returnTypeIsBoolean);
    }
}
