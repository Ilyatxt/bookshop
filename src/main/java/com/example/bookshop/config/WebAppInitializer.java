package com.example.bookshop.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Инициализатор веб-приложения без использования web.xml.
 * Реализует интерфейс WebApplicationInitializer для регистрации
 * контекста Spring и DispatcherServlet.
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(WebMvcConfig.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        servletContext.setInitParameter("defaultHtmlEscape", "true");
        servletContext.setInitParameter("spring.profiles.active", "default");

        servletContext.addFilter("characterEncodingFilter", 
                       new org.springframework.web.filter.CharacterEncodingFilter())
                .addMappingForUrlPatterns(null, false, "/*");
    }
}
