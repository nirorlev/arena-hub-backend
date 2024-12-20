package com.threeatom.guidecore.service;

public interface FrontendVersionService {

    String getVersion(String requestedVersion, Integer masterId);
}