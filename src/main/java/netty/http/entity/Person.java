package netty.http.entity;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/3
 */
public class Person {

    private String no;
    private String name;
    private Info info;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
