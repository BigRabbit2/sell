
import java.math.BigDecimal;
import java.util.Date;

public class User {

    private Integer id;

    private String userId;

    private BigDecimal balance;

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

    public String getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}