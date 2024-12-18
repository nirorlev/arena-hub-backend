package com.threeatom.guidecore.service;

public interface FeVersionService {

    String getVersion(String versionValue, Integer masterId);

    String findLatestVersion(Integer masterId);

    void updateVersion(String version, Integer masterId);
}