package com.sky.controller.user;

import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shoppingCart/sub")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    //实例调用要先声明字段 交给spring注入 不然shoppingCartService没东西认他
    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("删除用户购物车中的一个商品")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车：{}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
