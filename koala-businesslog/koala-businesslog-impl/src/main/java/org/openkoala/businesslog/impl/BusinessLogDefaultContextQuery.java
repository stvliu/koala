package org.openkoala.businesslog.impl;

import com.dayatang.domain.InstanceFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.openkoala.businesslog.QueryMethodConvertArgException;
import org.openkoala.businesslog.QueryMethodInvokeException;
import org.openkoala.businesslog.common.ContextQueryHelper;
import org.openkoala.businesslog.config.BusinessLogContextQuery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.*;

/**
 * User: zjzhai
 * Date: 12/4/13
 * Time: 11:35 AM
 */
public class BusinessLogDefaultContextQuery implements BusinessLogContextQuery {

    private String contextKey;

    private String beanClassName;

    private String beanName;

    private String methodSignature;

    /**
     * 方法调用的参数
     */
    private List<String> args;

    private Map<String, Object> context;


    public BusinessLogDefaultContextQuery() {
    }

    @Override
    public Map<String, Object> queryInContext(Map<String, Object> aContext) {
        context = new HashMap<String, Object>();
        if (null != aContext) {
            context.putAll(aContext);
        }
        context.put(contextKey, invoke(getBean(), getMethodParams()));
        return context;
    }

    private Object[] getMethodParams() {
        if (null == args || args.isEmpty()) {
            return null;
        }
        Object[] result = new Object[args.size()];
        for (int i = 0; i < getMethodParamTypes().size(); i++) {
            Class clasz = getMethodParamClasses()[i];
            result[i] = convertArg(args.get(i), clasz);
        }

        return result;
    }

    private List<String> getMethodParamTypes() {
        return ContextQueryHelper.getMethodParamTypes(methodSignature);
    }


    private String getMethodName() {
        return ContextQueryHelper.getMethodName(methodSignature);
    }

    private Method getMethodInstance() {
        return ContextQueryHelper.getMethodInstanceOf(beanClassName, getMethodName(), getMethodParamClasses());
    }

    private Class[] getMethodParamClasses() {
        List<String> types = ContextQueryHelper.getMethodParamTypes(methodSignature);
        return ContextQueryHelper.getMethodParamClasses(types);
    }

    private Object getBean() {
        try {
            if (isStaticMethodWillInvoke()) {
                return getClassOfBean().newInstance();
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (null != beanName && !"".equals(beanName.trim())) {
            return InstanceFactory.getInstance(getClassOfBean(), beanName);
        }
        return InstanceFactory.getInstance(getClassOfBean());

    }

    private Class getClassOfBean() {
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isStaticMethodWillInvoke() {
        return Modifier.isStatic(getMethodInstance().getModifiers());
    }


    // TODO 需要移动到ContextQueryHelper类中，并增加多更多类型的支持
    private Object convertArg(String arg, Class aClass) {
        return ContextQueryHelper.contextQueryArgConvertStringToObject(arg, aClass, context);
    }


    private Object invoke(Object methodObject, Object... params) {
        try {
            if (null == params) {
                return getMethodInstance().invoke(methodObject);
            }
            return getMethodInstance().invoke(methodObject, params);
        } catch (IllegalAccessException e) {
            throw new QueryMethodInvokeException(e);
        } catch (InvocationTargetException e) {
            throw new QueryMethodInvokeException(e);
        }
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getContextKey() {
        return contextKey;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public List<String> getArgs() {
        return args;
    }
}