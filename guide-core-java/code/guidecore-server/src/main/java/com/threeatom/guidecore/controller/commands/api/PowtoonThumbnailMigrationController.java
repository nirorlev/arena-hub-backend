package com.threeatom.guidecore.controller.commands.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.threeatom.guidecore.constant.EventUnifyType;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
    value = "/api/v2/commands/migrate-powtoon-thumbnails",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Thumbnail migration", produces = MediaType.APPLICATION_JSON_VALUE)
public class PowtoonThumbnailMigrationController {

    private static final String KALTURA_THUMBNAIL_URL_TEMPLATE = "https://cfvod.kaltura.com/p/%s/sp/%s/thumbnail/entry_id/%s";

    @Autowired
    private SysFileService sysFileService;

    private String[] getVideoHostingDataFromPlayerUrl(String playerUrl) {
        try {
            String partnerId = null;
            String uiConfId = null;
            String entryId = null;
            URL url = new URL(playerUrl);
            String[] pathParts = url.getPath().split("/");
            for (int i = 0; i < pathParts.length; i++) {
                if (pathParts[i].equals("partner_id")) {
                    partnerId = pathParts[i+1];
                } else if (pathParts[i].equals("uiconf_id")) {
                    uiConfId = pathParts[i+1];
                } else if (pathParts[i].equals("entry_id")) {
                    entryId = pathParts[i+1];
                }
            }
            if (partnerId == null || uiConfId == null || entryId == null) {
                return null;
            }
            return new String[]{partnerId, uiConfId, entryId};
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private void migrateSysFileThumbnail(SysFile sysFile) {
        log.info("Migrating thumbnail for sysFile: " + sysFile.getId());
        log.info("Old thumbnail URL: " + sysFile.getThumbNailUrl());

        String playerUrl = sysFile.getFileUrl();
        if (playerUrl == null) {
            log.info("No player URL found for sysFile: " + sysFile.getId());
            return;
        }
        log.info("Player URL: " + playerUrl);

        String[] videoHostingData = getVideoHostingDataFromPlayerUrl(playerUrl);
        if (videoHostingData == null) {
            log.info("Could not extract video hosting data from player URL: " + playerUrl);
            return;
        }
        String partnerId = videoHostingData[0];
        String uiConfId = videoHostingData[1];
        String entryId = videoHostingData[2];
        String newThumbnailUrl = String.format(KALTURA_THUMBNAIL_URL_TEMPLATE, partnerId, uiConfId, entryId);
        log.info("SysFile updated " + sysFile.getId() + ". New thumbnail URL: " + newThumbnailUrl);
        sysFile.setThumbNailUrl(newThumbnailUrl);
        sysFileService.updateById(sysFile);
    }

    @PostMapping
    @ApiOperation(value = "Migrates powtoon thumbnails", httpMethod = "POST")
    public ResponseEntity<JSONObject> migratePowtoonThumbnails(HttpServletRequest request) {
        QueryWrapper<SysFile> sysFileQuery = new QueryWrapper<>();
        sysFileQuery.eq("file_type_index", EventUnifyType.powtoonFileTypeIndex);
        sysFileQuery.like("thumb_nail_url", "%.s3.amazonaws.com/%");
        List<SysFile> sysFiles = sysFileService.list(sysFileQuery);
        for (SysFile sysFile : sysFiles) {
            migrateSysFileThumbnail(sysFile);
        }
        JSONObject result = new JSONObject();
        result.put("migratedThumbnails", sysFiles.size());
        return ResponseEntity.ok().body(result);
    }
}
