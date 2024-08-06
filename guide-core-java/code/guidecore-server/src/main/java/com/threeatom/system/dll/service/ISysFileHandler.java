package com.threeatom.system.dll.service;

public interface ISysFileHandler<T> extends ISysHandler<T> {
    String getUserFolder();

    String getSysFolder();
}
