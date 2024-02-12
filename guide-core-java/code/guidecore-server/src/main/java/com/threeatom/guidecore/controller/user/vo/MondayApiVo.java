package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

@Data
public class MondayApiVo {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String portalName;
    private String codeName;
    private String invoiceUrl;
    // 如果是0，代表是通过code加入新门户，如果是1代表是支付
    private Integer paidFlag;
    private Long boardId;
    private String title;
    private String settings_str;
}
