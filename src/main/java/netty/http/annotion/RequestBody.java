package netty.http.annotion;

import java.lang.annotation.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/4
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
    String value() default "";
}
