package netty.http.annotion;

import java.lang.annotation.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/1
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
