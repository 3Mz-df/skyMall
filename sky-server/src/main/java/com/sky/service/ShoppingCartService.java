package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

public interface ShoppingCartService {
    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
     void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
