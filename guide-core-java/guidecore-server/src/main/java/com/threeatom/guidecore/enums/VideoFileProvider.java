package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum VideoFileProvider {
    YOUTUBE(13),
    VIMEO(14),
    WISTIA(15),
    OTHER(-1);

    private final int fileTypeIndex;

    public static VideoFileProvider fromIndex(Integer index) {
        if (index == null) {
            return OTHER;
        }

        for (VideoFileProvider value : values()) {
            if (value.fileTypeIndex == index) {
                return value;
            }
        }

        return OTHER;
    }
}
