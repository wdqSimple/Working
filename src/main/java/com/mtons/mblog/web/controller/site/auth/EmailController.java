package com.mtons.mblog.web.controller.site.auth;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.service.MailService;
import com.mtons.mblog.modules.service.SecurityCodeService;
import com.mtons.mblog.modules.service.UserService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author langhsu on 2015/8/14.
 */
@RestController
@RequestMapping("/email")
public class EmailController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;
    @Autowired
    private SecurityCodeService securityCodeService;

    private static final String EMAIL_TITLE = "[{0}]您正在使用邮箱安全验证服务";

    @GetMapping("/send_code")
    public Result sendCode(String email, @RequestParam(name = "type", defaultValue = "1") Integer type) {

        //将用户账号与验证码一起发送
        UserVO user = null;

        //判断邮箱是否为null
        Assert.hasLength(email, "请输入邮箱地址");
        Assert.notNull(type, "缺少必要的参数");

        //接受前台传入email
        String key = email;

        switch (type) {
            case Consts.CODE_BIND:
                //
                AccountProfile profile = getProfile();
                Assert.notNull(profile, "请先登录后再进行此操作");
                key = String.valueOf(profile.getId());
                break;
            case Consts.CODE_FORGOT:
                //
                user = userService.getByEmail(email);
                Assert.notNull(user, "账户不存在");
                key = String.valueOf(user.getId());
                break;
            case Consts.CODE_REGISTER:
                //注册
                key = email;
                break;
        }

        //生成6位验证码
        String code = securityCodeService.generateCode(key, type, email);
        System.out.println("生成的验证码"+code);
        Map<String, Object> context = new HashMap<>();
        //邮件内容
        context.put("code", code);

        //邮件title
        String title = MessageFormat.format(EMAIL_TITLE, siteOptions.getValue("site_name"));

        //发送邮件
        mailService.sendTemplateEmail(email, title, Consts.EMAIL_TEMPLATE_CODE, context);

        return Result.successMessage("邮件发送成功");
    }

}
