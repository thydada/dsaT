package com.example.gpot.controller;

import com.example.gpot.dto.ApiResponse;
import com.example.gpot.dto.LoginRequest;
import com.example.gpot.dto.LoginResponse;
import com.example.gpot.entity.Admin;
import com.example.gpot.entity.Employee;
import com.example.gpot.entity.User;
import com.example.gpot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 显示登录页面（Vue.js版本）
     */
    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    /**
     * 显示传统登录页面（Thymeleaf版本）
     */
    @GetMapping("/login-traditional")
    public String loginPageTraditional() {
        return "login";
    }

    /**
     * REST API登录接口
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<ApiResponse<LoginResponse>> loginApi(@RequestBody LoginRequest loginRequest) {
        try {
            String userType = loginRequest.getUserType();
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            switch (userType) {
                case "admin":
                    Admin admin = authService.loginAdmin(username, password);
                    if (admin != null) {
                        LoginResponse response = new LoginResponse(true, "管理员登录成功！",
                            userType, admin.getId(), admin.getRealName(), admin.getUsername(), admin);
                        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                    }
                    break;

                case "employee":
                    Employee employee = authService.loginEmployee(username, password);
                    if (employee != null) {
                        LoginResponse response = new LoginResponse(true, "员工登录成功！",
                            userType, employee.getId(), employee.getRealName(), employee.getUsername(), employee);
                        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                    }
                    break;

                case "user":
                    User user = authService.loginUser(username, password);
                    if (user != null) {
                        LoginResponse response = new LoginResponse(true, "用户登录成功！",
                            userType, user.getId(), user.getRealName(), user.getUsername(), user);
                        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                    }
                    break;
            }

            return ResponseEntity.badRequest()
                .body(ApiResponse.error(userType + "用户名或密码错误！"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("登录过程中发生错误：" + e.getMessage()));
        }
    }

    /**
     * 传统页面登录处理（兼容旧版本）
     */
    @PostMapping("/login")
    public String loginTraditional(@RequestParam String userType,
                       @RequestParam String username,
                       @RequestParam String password,
                       Model model) {

        String successMessage = "";
        String userInfo = "";

        try {
            switch (userType) {
                case "admin":
                    Admin admin = authService.loginAdmin(username, password);
                    if (admin != null) {
                        successMessage = "管理员登录成功！";
                        userInfo = "欢迎管理员：" + admin.getRealName() + "（用户名：" + admin.getUsername() + "）";
                    } else {
                        model.addAttribute("error", "管理员用户名或密码错误！");
                        return "login";
                    }
                    break;

                case "employee":
                    Employee employee = authService.loginEmployee(username, password);
                    if (employee != null) {
                        successMessage = "员工登录成功！";
                        userInfo = "欢迎员工：" + employee.getRealName() + "（用户名：" + employee.getUsername() +
                                  "，部门：" + employee.getDepartment() + "，职位：" + employee.getPosition() + "）";
                    } else {
                        model.addAttribute("error", "员工用户名或密码错误！");
                        return "login";
                    }
                    break;

                case "user":
                    User user = authService.loginUser(username, password);
                    if (user != null) {
                        successMessage = "用户登录成功！";
                        userInfo = "欢迎用户：" + user.getRealName() + "（用户名：" + user.getUsername() + "）";
                    } else {
                        model.addAttribute("error", "用户用户名或密码错误！");
                        return "login";
                    }
                    break;

                default:
                    model.addAttribute("error", "无效的用户类型！");
                    return "login";
            }

            model.addAttribute("successMessage", successMessage);
            model.addAttribute("userInfo", userInfo);
            model.addAttribute("userType", userType);
            return "login-success";

        } catch (Exception e) {
            model.addAttribute("error", "登录过程中发生错误：" + e.getMessage());
            return "login";
        }
    }

    /**
     * 返回登录页面
     */
    @GetMapping("/back")
    public String backToLogin() {
        return "index";
    }
}