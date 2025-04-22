package com.threeatom.guidecore.mapping;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.mapstruct.Mapper;

@Mapper
public interface DateMapping {
    default OffsetDateTime map(Date value) {
        return value.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
