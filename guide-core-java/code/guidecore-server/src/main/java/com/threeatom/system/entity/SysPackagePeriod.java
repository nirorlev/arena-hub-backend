//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SysPackagePeriod implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer QTY;
    private String type;
    private BigDecimal price;
    private Date expired;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getQTY() {
        return QTY;
    }

    public void setQTY(Integer QTY) {
        this.QTY = QTY;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "SysPackagePeriod{"
                + "id="
                + id
                + ", QTY="
                + QTY
                + ", type='"
                + type
                + '\''
                + ", price="
                + price
                + ", expired="
                + expired
                + '}';
    }
}
