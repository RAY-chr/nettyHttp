package netty.dao.annotion;

import java.lang.annotation.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/9
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {
}
