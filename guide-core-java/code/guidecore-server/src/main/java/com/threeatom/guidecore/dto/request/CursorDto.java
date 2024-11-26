package com.threeatom.guidecore.dto.request;

import java.time.OffsetDateTime;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@AllArgsConstructor
public class CursorDto {
    private static final String SEPARATOR = "/";

    private Integer id;
    private OffsetDateTime updateTime;

    public static CursorDto decode(String encodedCursor) {
        if (StringUtils.isBlank(encodedCursor)) {
            return new CursorDto(1, OffsetDateTime.MAX);
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(encodedCursor));
            String[] parts = decoded.split(SEPARATOR);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid cursor format. The separator is missing.");
            }

            OffsetDateTime updateTime = OffsetDateTime.parse(parts[0]);
            Integer id = Integer.parseInt(parts[1]);
            return new CursorDto(id, updateTime);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode cursor", e);
        }
    }

    public String encode() {
        String rawCursor = updateTime.toString() + SEPARATOR + id;
        return Base64.getEncoder().encodeToString(rawCursor.getBytes());
    }
}
