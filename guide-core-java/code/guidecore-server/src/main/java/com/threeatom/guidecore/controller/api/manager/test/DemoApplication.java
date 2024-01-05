/*
package com.threeatom.guidecore.controller.api.manager.test;

*/
/*import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiException;
import io.permit.sdk.api.models.RoleAssignmentList;
import io.permit.sdk.enforcement.AssignedRole;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;*//*

import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import io.permit.sdk.openapi.models.*;
import io.permit.sdk.util.Context;
import io.swagger.annotations.Api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


*/
/**
 * @author Administrator
 * @title: Test
 * @projectName jeeplus-core
 * @description: TODO
 * @date 2022/8/12/01221:19
 *//*

@RestController
@RequestMapping("/tests")
@Api(tags = "管理平台功能")
//@SpringBootApplication
public class DemoApplication {
*/
/*
    final Permit permit;
    final UserRead user;

    public DemoApplication() {
        // init the permit SDK
        this.permit = new Permit(
                new PermitConfig.Builder(
                        "permit_key_udA2LsMaHVC7nMD08bSKJ3iiCaFgGgGMJCOPg0ENlHG2Slz7wa2g0Vf9CnH9xwgoYGL7fMeD2S9djpER3p5Con"
                )
                        .withPdpAddress("http://47.111.191.189:7766")
                        .withDebugMode(true)
                        .build()
        );

        try {
            HashMap<String, Object> userAttributes = new HashMap<>();
            userAttributes.put("adminGroups","resource.groupId");
            userAttributes.put("isOrgAdmin","False");
            // typically you would sync a user to the permission system
            // and assign an initial role when the user signs up to the system
            this.user = permit.api.users.sync(
                    // the user "key" is any id that identifies the user uniquely
                    // but is typically taken straight from the user JWT `sub` claim
                    new UserCreate("kyle.wong@guide.com")
                            .withEmail("kyle.wong@guide.com")
                            .withFirstName("Kyle")
                            .withLastName("Wong")
                            .withAttributes(userAttributes)
            ).getResult();
            // you can use this api call to assign a role when a user first logs in
            // permit.api.users.assignRole(user.key, "<ROLE KEY>", "<TENANT KEY>");

        } catch (IOException | PermitApiError | PermitContextError e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/")
    ResponseEntity<String> home() throws IOException, PermitApiError, PermitContextError {
        // is `user` allowed to do `action` on `resource`?

        User user = User.fromString(permit.api.users.get("kyle.wong@guide.com").key); // pass the user *key* to init a user object from string

        String action = "addcontent";
        Resource resource = new Resource.Builder("ContentGroup")
                // you can set a specific tenant for the permission check
                 .withTenant("org1")
                .build();

        // to run a permission check, use permit.check()
        boolean permitted = permit.check(user, action, resource);

        if (permitted) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    "Kyle Wong is PERMITTED to addcontent Course!"
            );
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    "Kyle Wong is NOT PERMITTED to addcontent Course!"
            );
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }*//*

}
*/
