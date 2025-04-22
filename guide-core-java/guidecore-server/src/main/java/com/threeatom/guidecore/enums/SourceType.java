package com.threeatom.guidecore.enums;

public enum SourceType {
    COURSE,
    CHANNEL,
    PLAYLIST;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
