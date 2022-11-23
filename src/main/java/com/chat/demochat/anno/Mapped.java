package com.chat.demochat.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapped
{
    String value();

    MappedEnum dest() default MappedEnum.WEB;
}
