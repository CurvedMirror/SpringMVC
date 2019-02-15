package cn.smbms.tools;

import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jie
 * @date 2019/2/15 -10:32
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("app*.xml");
        UserService userService = ctx.getBean(UserService.class);
        User user = userService.login("admin", "1234567");
        System.out.println(user.getPhone());
    }
}
