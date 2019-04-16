package ru.vonabe.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Misha Pazin 1995/05/06
 */

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DialogViewAnnotation {
    SearchAnnotation.TypeDialog value();
}
