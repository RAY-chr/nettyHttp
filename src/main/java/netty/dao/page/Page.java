package netty.dao.page;

import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/16
 */
public class Page<T> {
    private long current;
    private long size;
    private long total;
    private List<?> records;

    public Page(long current, long size) {
        this.current = current;
        this.size = size;
    }

    public Page() {
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return (List<T>) records;
    }

    public void setRecords(List<?> records) {
        this.records = records;
    }
}
