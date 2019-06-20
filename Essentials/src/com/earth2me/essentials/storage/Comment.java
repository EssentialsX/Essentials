package com.earth2me.essentials.storage;

import java.lang.annotation.*;


/**
 * <p>Comment class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
@Target(ElementType.FIELD) @Documented @Retention(RetentionPolicy.RUNTIME) public @interface Comment {
    String[] value() default "";
}
