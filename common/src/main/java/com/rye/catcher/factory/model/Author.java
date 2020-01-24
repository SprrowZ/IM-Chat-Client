package com.rye.catcher.factory.model;

/**
 * 基础用户接口,交给User实现
 * @version 1.0.0
 */
public interface Author {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getPortrait();

    void setPortrait(String portrait);
}
