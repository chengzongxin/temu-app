package com.czx.service;

import com.czx.pojo.Dynamic;

public interface DynamicService {
    void insert(Dynamic dynamic);

    Dynamic[] list();

    void update(Dynamic dynamic);
}
