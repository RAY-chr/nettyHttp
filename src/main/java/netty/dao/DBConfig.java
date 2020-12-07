package netty.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class DBConfig {
    private static final Properties properties = new Properties();

    public static String getString(String key) {
        return properties.getProperty(key);
    }

    static {
        InputStream in = DBConfig.class.getResourceAsStream("/db.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
