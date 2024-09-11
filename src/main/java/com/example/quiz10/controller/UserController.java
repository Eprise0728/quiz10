package com.example.quiz10.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.service.ifs.UserService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.UserReq;

@RestController

public class UserController {
	
	@Autowired
	private UserService userSerivce;
	
	@PostMapping(value = "user/register")
	public BasicRes register(@Valid @RequestBody UserReq req){
		return userSerivce.register(req);
	}
	
	@PostMapping(value = "user/login")
	public BasicRes login(@Valid @RequestBody UserReq req,HttpSession session) {
		//session的存活時間預設為30分鐘，可以透過以下方法設定時間長短(括號中的數字單位是秒)
		//數字0或負數表示該session不會過期
		//session.setMaxInactiveInterval(300);
		BasicRes res =  userSerivce.login(req);
		if(res.getCode()==200) {
			//若登入成功，把使用者名稱暫存在session中
			//若每個client與server之間的session都不一樣
			session.setAttribute("user_name", req.getName());
		}
		return res;
	}
	
	//因為沒有req所以用GetMapping
	@GetMapping(value = "user_logout")
	public BasicRes logout(HttpSession session) {
		//logout 就是要讓彼此之間通訊用的session失效(過期)
		session.invalidate();
		return new BasicRes(ResMessage.SUCCESS.getCode(),ResMessage.SUCCESS.getMessage());
	}
}
