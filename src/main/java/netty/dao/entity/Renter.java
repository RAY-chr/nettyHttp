package netty.dao.entity;

import netty.dao.annotion.TableId;
import netty.dao.annotion.TableName;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
@TableName("renter")
public class Renter {

    @TableId("renter_id")
    private String renter_id;
    private String renter_name;

    public Renter(String renter_id, String renter_name) {
        this.renter_id = renter_id;
        this.renter_name = renter_name;
    }

    public String getRenter_id() {
        return renter_id;
    }

    public void setRenter_id(String renter_id) {
        this.renter_id = renter_id;
    }

    public String getRenter_name() {
        return renter_name;
    }

    public void setRenter_name(String renter_name) {
        this.renter_name = renter_name;
    }
}
