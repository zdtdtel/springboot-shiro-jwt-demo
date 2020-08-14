package com.codingos.shirojwt.controller;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codingos.shirojwt.common.Result;
import com.codingos.shirojwt.common.ResultEnum;
import com.codingos.shirojwt.constant.Constant;
import com.codingos.shirojwt.exception.CustomException;

@Controller
public class ShiroJwtDemoController {

	@RequestMapping("/tologin")
	public String tologin() {
		return "login";
	}
	
	@PostMapping("/login")
	public String login(HttpServletResponse response) {
		Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_SECRET);
		Date date = new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 7);
		String jwt = JWT.create()
				.withClaim("username", "Tom")	// 用户名
				.withClaim("nickname", Base64Utils.encodeToUrlSafeString("汤姆".getBytes()))   // 中文名
				.withExpiresAt(date)   // 过期时间
				.sign(algorithm);
		response.addCookie(new Cookie("shiro-jwt", jwt));
		return "redirect:indexpage.html";
	}
	
	@GetMapping("/testRole")
	@RequiresRoles(value = {"role-1", "role-2"}, logical = Logical.OR)
	@ResponseBody
	public String testRole() {
		return "testRole";
	}
	
	@GetMapping("/testPermission")
	@RequiresPermissions(value = {"perm-1", "perm-2"}, logical = Logical.AND)
	@ResponseBody
	public String testPermission() {
		return "testPermission";
	}
	
	/**
	 * 用来处理 shiro filter 中的异常, 在发生异常的时候 forward 到controller, 然后由 controller 的统一异常处理
	 */
	@RequestMapping("/exception")
	public void exception(HttpServletRequest request) {
		throw new CustomException(new Result<>(ResultEnum.ERROR.getCode(), (String) request.getAttribute("msg")));
	}
}
