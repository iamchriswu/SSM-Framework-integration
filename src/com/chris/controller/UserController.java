package com.chris.controller;

import com.chris.pojo.User;
import com.chris.pojo.UserCustom;
import com.chris.pojo.UserQueryVo;
import com.chris.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public String register(User user, Model model) throws Exception{
        if (userService.insertUser(user) == 1) {
            model.addAttribute("info","success");
        } else {
            model.addAttribute("info", "error");
        }
        return "message";
    }

    @RequestMapping("/login")
    public String login(String userName, String password, Model model) throws Exception {
        //构建User的包装对象
        System.out.println(userName + " " + password);
        UserCustom userCustom = new UserCustom();
        userCustom.setUserName(userName);
        userCustom.setPassword(password);
        UserQueryVo userQueryVo = new UserQueryVo();
        userQueryVo.setUserCustom(userCustom);

        if (userService.findUserByUserNameAndPassword(userQueryVo) != null) {
            model.addAttribute("info","success");
        } else {
            model.addAttribute("info", "error");
        }
        return "message";
    }
}
