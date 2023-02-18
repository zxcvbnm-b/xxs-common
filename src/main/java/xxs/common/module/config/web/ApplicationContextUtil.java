package xxs.common.module.config.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 容器对象上下文
 *
 * @author xxs
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context != null ? context.getBean(requiredType) : null;
    }

    public static Object getBean(String name) {
        return context != null ? context.getBean(name) : null;
    }

}
