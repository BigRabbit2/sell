import java.math.BigDecimal;
import java.util.Date;

public class Trip {

    private Integer id;

    private String tripId;

    private Integer count;

    private BigDecimal price;

    private Date tripDate;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public Integer getId() {
        return id;
    }

    public String getTripId() {
        return tripId;
    }

    public Integer getCount() {
        return count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getTripDate() {
        return tripDate;
    }
}