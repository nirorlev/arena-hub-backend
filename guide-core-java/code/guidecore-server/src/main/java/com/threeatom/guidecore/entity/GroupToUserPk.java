package com.threeatom.guidecore.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupToUserPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String powtoonGroupCode;
    private Integer userId;
}
