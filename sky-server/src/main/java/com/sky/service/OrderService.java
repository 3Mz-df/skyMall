package com.sky.service;

import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 用户下单
     * @param orderSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO);
}
