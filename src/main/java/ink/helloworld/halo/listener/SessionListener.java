package ink.helloworld.halo.listener;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentInfo;
import cn.hutool.http.useragent.UserAgentUtil;
import ink.helloworld.halo.logging.Logger;
import ink.helloworld.halo.service.VisitsService;
import ink.helloworld.halo.utils.SessionUtil;
import ink.helloworld.halo.utils.SpringUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.HashSet;

@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    Logger logger = Logger.getLogger(getClass());

    @Override
    public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
        logger.info("--attributeAdded--");
        @SuppressWarnings("unused")
        HttpSession session = httpSessionBindingEvent.getSession();
        logger.info("key----:" + httpSessionBindingEvent.getName());
        logger.info("value---:" + httpSessionBindingEvent.getValue());

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
        logger.info("--attributeRemoved--");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
        logger.info("--attributeReplaced--");
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.info("---sessionCreated----");
        HttpSession session = event.getSession();
        ServletContext application = session.getServletContext();
        // 在application范围由一个HashSet集保存所有的session
        @SuppressWarnings("unchecked")
        HashSet<HttpSession> sessions = (HashSet<HttpSession>) application.getAttribute("sessions");
        if (sessions == null) {
            sessions = new HashSet<HttpSession>();
            application.setAttribute("sessions", sessions);
        }
        // 新创建的session均添加到HashSet集中
        sessions.add(session);
        // 可以在别处从application范围中取出sessions集合
        // 然后使用sessions.size()获取当前活动的session数，即为“在线人数”

        //添加新建的session到MySessionContext中;
        SessionUtil.AddSession(event.getSession());

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        VisitsService visitsService = SpringUtil.getBean(VisitsService.class);
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));

        String browser = userAgent.getBrowser().getName() + " " + userAgent.getVersion();
        String os = userAgent.getOs().getPattern().toString();
        visitsService.save(request.getRemoteAddr(), browser, os, request.getRequestURI(), request.getHeader("User-Agent"));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {
        logger.info("---sessionDestroyed----");
        HttpSession session = event.getSession();
        logger.info("deletedSessionId: " + session.getId());
        System.out.println(session.getCreationTime());
        System.out.println(session.getLastAccessedTime());
        ServletContext application = session.getServletContext();
        HashSet<?> sessions = (HashSet<?>) application.getAttribute("sessions");
        // 销毁的session均从HashSet集中移除
        sessions.remove(session);

        //添加新建的session到MySessionContext中;
        SessionUtil.DelSession(session);
    }

}