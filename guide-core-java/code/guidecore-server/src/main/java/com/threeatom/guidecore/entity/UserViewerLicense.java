package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName(value = "user_viewer_license", autoResultMap = true)
public class UserViewerLicense implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer publishChannelLimit;
    private Integer publishPlaylistLimit;
    private Integer privateChannelCount;
    private Integer privatePlaylistCount;
    private Integer publishChannelCount;
    private Integer publishPlaylistCount;
    private boolean active;
}
