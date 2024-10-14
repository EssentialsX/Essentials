package net.essentialsx.providers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProviderData {
    /**
     * A brief description of when this specific provider is used (MC Version, Server Software) and its name.
     */
    String description();

    /**
     * If there is multiple providers for a given type that pass their {@link ProviderTest}, the one with the highest weight will be used.
     * @return the weight of the provider.
     */
    int weight() default 0;
}
