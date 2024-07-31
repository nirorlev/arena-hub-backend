package com.threeatom.guidecore.controller.user.vo;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ScheduleTable {

    @JSONField(format = "yyyy-MM-dd")
    private Date date;

    private Set<Integer> vids;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Integer> getVids() {
        return vids;
    }

    public void setVids(Set<Integer> vids) {
        this.vids = vids;
    }

    public Set<Integer> addVid(Integer vid) {
        if (vids == null) {
            vids = new HashSet<Integer>();
        }
        vids.add(vid);
        return vids;
    }

    @Override
    public String toString() {
        return date.toString() + vids.size();
    }
}
