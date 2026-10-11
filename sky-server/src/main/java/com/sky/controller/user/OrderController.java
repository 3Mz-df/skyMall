package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "c端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单接口")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单, 参数为：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 订单查询 用户端
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status){
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}") // ① {id} 是个占位坑："路径这一段是参数，名叫 id"
    @ApiOperation("订单详情查询")
    public Result<OrderVO> details(@PathVariable("id") Long id){ // ② 把坑里的值抠出来，塞进形参 id
        OrderVO orderVO = orderService.details(id);
          //前端请求：/orderDetail/101
          //               ↓
          //{ id } 坑里装着 "101"
          //               ↓
          //@PathVariable 抠出来 → 塞进形参 Long id → id = 101
          //               ↓
          //orderService.details(101) → 查出订单 101 的详情
        return Result.success(orderVO);
        //Query 参数就裸收，Path 参数就 @PathVariable，Body 参数就 @RequestBody。 接口文档 Path 里带`{id}` 这种花括号，
        // 就等于提醒你用 @PathVariable 。  路径中间抠值用 PathVariable，尾巴上取值啥也不用，身体里读值用 RequestBody 。
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("订单取消")
    public Result cancel(@PathVariable("id") Long id) throws Exception{
        orderService.userCancelById(id);
        return Result.success();
    }
}
