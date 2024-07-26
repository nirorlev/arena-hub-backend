package com.threeatom.system.dll.service;

public interface ISysHandler<T> {
    T getService();

    String getContextName();
}
