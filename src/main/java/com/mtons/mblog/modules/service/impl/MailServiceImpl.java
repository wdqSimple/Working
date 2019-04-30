package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.base.lang.MtonsException;
import com.mtons.mblog.config.SiteOptions;
import com.mtons.mblog.modules.service.MailService;
import freemarker.template.Template;
import io.github.biezhi.ome.OhMyEmail;
import io.github.biezhi.ome.SendMailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;
import java.util.Properties;

/**
 * @author : langhsu
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private SiteOptions siteOptions;
    @Autowired
    private TaskExecutor taskExecutor;

    //初始邮箱配置信息
    @Override
    public void config() {
        //账号密码 在系统后台 个人配置
        String mailHost = siteOptions.getValue("mail_smtp_host");
        String mailUsername = siteOptions.getValue("mail_smtp_username");
        String mailPassowrd = siteOptions.getValue("mail_smtp_password");

//        //邮件发送主机
//        String mailHost = "smtp.qq.com";
//        //邮件发送人
//        String mailUsername = "764569359@qq.com";
////        String mailPassowrd = "wangdongqi123.";
//        //发送人策略码
//        String mailPassowrd = "qmayecvrprulbcfh";
        if (StringUtils.isNoneBlank(mailHost, mailUsername, mailPassowrd)) {
            //如果defaultConfig(true) 将邮件发送时 所有信息在控制台打印
            //如果是false 控制台不会打印发送邮件信息 效率高于控制台打印
            final Properties properties = OhMyEmail.defaultConfig(false);
            //设置邮箱主机
            properties.setProperty("mail.smtp.host", mailHost);
            //将邮件发送人、策略码、主机都保存  不会验证发送人 跟策略码是否一致 在发送邮件的时候 才会验证
            OhMyEmail.config(properties, mailUsername, mailPassowrd);
        } else {
            log.error("邮件服务配置信息未设置, 请在后台系统配置中进行设置");
        }
    }

    //发送邮件模板
    @Override
    public void sendTemplateEmail(String to, String title, String template, Map<String, Object> content) {

        String text = render(template, content);
        String from = siteOptions.getValue("site_name");

        taskExecutor.execute(() -> {
            try {
                OhMyEmail.subject(title)
                        .from(from)
                        .to(to)
                        .html(text)
                        .send();
            } catch (SendMailException e) {
                log.error(e.getMessage(), e);
            }
            log.info("email: {} send success", to);
        });
    }

    //邮件网页
    private String render(String templateName, Map<String, Object> model) {
        try {
            Template t = freeMarkerConfigurer.getConfiguration().getTemplate(templateName, "UTF-8");
            t.setOutputEncoding("UTF-8");
            return FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
        } catch (Exception e) {
            throw new MtonsException(e.getMessage(), e);
        }
    }
}
