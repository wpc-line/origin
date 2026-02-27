package org.wpc.controller;

import org.wpc.entity.User;
import org.wpc.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserMapper userMapper;

    @RequestMapping("/getUser")
    public String getUser(Integer id){
        User user = userMapper.selectById(id);
        return "user";
    }
}
