package xxs.common.module.utils.bean;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 *
 * 自定义拷贝工具类，用来拷贝list以及自定义转换方法等
 */
public class CommonBeanUtils extends org.springframework.beans.BeanUtils {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Method readMethod = pd.getReadMethod();
            Annotation assertTrue = readMethod.getAnnotation(AssertTrue.class);
            Annotation assertFalse = readMethod.getAnnotation(AssertFalse.class);
            if (assertTrue != null || assertFalse != null) {
                continue;
            }
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 数据拷贝
     *
     * @param source: 数据源类
     * @param target: 目标类::new(eg: UserDto::new)
     * @return
     */
    public static <S, T> T copyProperties(S source, Supplier<T> target) {
        T t = target.get();
        org.springframework.beans.BeanUtils.copyProperties(source, t);
        return t;
    }

    /**
     * 数据拷贝
     *
     * @param source:   数据源类
     * @param target:   目标类::new(eg: UserDto::new)
     * @param callBack: 回调函数
     * @return
     */
    public static <S, T> T copyProperties(S source, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
        T t = target.get();
        org.springframework.beans.BeanUtils.copyProperties(source, t);
        if (callBack != null) {
            // 回调
            callBack.callBack(source, t);
        }
        return t;
    }

    /**
     * 集合数据的拷贝
     *
     * @param sources: 数据源类
     * @param target:  目标类::new(eg: UserDto::new)
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }


    /**
     * 带回调函数的集合数据的拷贝（可自定义字段拷贝规则）
     *
     * @param sources:  数据源类
     * @param target:   目标类::new(eg: UserDto::new)
     * @param callBack: 回调函数
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
        if (sources == null || sources.size() == 0) {
            return new ArrayList<T>();
        }
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            list.add(t);
            if (callBack != null) {
                // 回调
                callBack.callBack(source, t);
            }
        }
        return list;
    }

    /**
     * @param src:   数据源对象
     * @param target ：目标对象
     *               封装同名称属性复制，但是空属性不复制过去
     */
    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static void setProperty(Object obj, String propertyName, Object value) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
        Method setMethod = pd.getWriteMethod();
        setMethod.invoke(obj, value);
    }

    public static Object getProperty(Object obj, String propertyName) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
        Method getMethod = pd.getReadMethod();
        return getMethod.invoke(obj);
    }

    public static void mapToObject(Map<String, Object> map, Object dto, boolean b) {

    }
}
