package com.earth2me.essentials.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate to Configurate that the annotated field should be
 * treated as null if it is treated as incomplete.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteIfIncomplete {
}
