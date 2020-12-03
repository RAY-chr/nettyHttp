package netty.http.annotion;

import java.lang.annotation.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
