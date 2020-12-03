package netty.http.annotion;

import java.lang.annotation.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
}
