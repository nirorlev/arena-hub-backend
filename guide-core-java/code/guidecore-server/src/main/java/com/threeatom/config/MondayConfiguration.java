package com.threeatom.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @title: MondayConfiguration
 * @projectName guidecore
 * @description: TODO
 * @date 2022/6/30/03014:43
 */

@Configuration
@ConfigurationProperties(prefix = "monday")
@Data
public class MondayConfiguration {
    @Value("${monday.apiKey:eyJhbGciOiJIUzI1NiJ9.eyJ0aWQiOjYyNjg0OTY3LCJ1aWQiOjc5NTgyMzQsImlhZCI6IjIwMjAtMDctMjRUMTY6MTY6MDAuMDAwWiIsInBlciI6Im1lOndyaXRlIiwiYWN0aWQiOjM2MzcwNDksInJnbiI6InVzZTEifQ.Lx2plgIe1JJwFztepPBzWZPott_2sXv0tUmvLXYPCHg}")
    private String mondayApiKey;

    @Value("${monday.boardId:3226335945}")
    private String boardId;

    @Value("${monday.groupId:new_group}")
    private String groupId;

    @Value("${monday.callMondayApiFlag:0}")
    private Integer callMondayApiFlag;
}
