package cn.smbms.interceptor;

import cn.smbms.pojo.User;
import cn.smbms.tools.Constants;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author jie
 * @date 2019/2/23 -9:58
 */
public class SysInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = Logger.getLogger(SysInterceptor.class);

    public boolean perHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handle) throws Exception {
        logger.debug("SysInterceptor preHandle");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.USER_SESSION);
        if (null == user){
            response.sendRedirect(request.getContextPath()+"/401.jsp");
            return  false;
        }
        return true;
    }
}
