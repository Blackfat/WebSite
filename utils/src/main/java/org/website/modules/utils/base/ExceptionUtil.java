package org.website.modules.utils.base;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.website.modules.utils.base.annotation.Nullable;

/**
 * Created by blackfat on 17/2/19.
 */
public class ExceptionUtil {

    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

    /**
     * 自定义非检查异常
     */
    public static class UncheckedException extends RuntimeException {

        public static final long serialVersionUID = 4140223302171577501L;

        public UncheckedException(Throwable cause) {
            super(cause);
        }

        @Override
        public String getMessage() {
            return super.getCause().getMessage();
        }
    }


    /**
     * 将CheckedException转换为RuntimeException重新抛出, 可以减少函数签名中的CheckExcetpion定义.
     * <p>
     * CheckedException会用UndeclaredThrowableException包裹，RunTimeException和Error则不会被转变.
     * <p>
     * from Commons Lange 3.5 ExceptionUtils.
     * <p>
     * 虽然unchecked()里已直接抛出异常，但仍然定义返回值，方便欺骗Sonar。因此本函数也改变了一下返回值
     * <p>
     * 示例代码:
     * <p>
     * <pre>
     * try{ ... }catch(Exception e){ throw unchecked(t); }
     *
     * @see ExceptionUtils#wrapAndThrow(Throwable)
     */
    public static RuntimeException unchecked(Throwable t) {

        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }

        if (t instanceof Error) {
            throw (Error) t;
        }

        throw new UncheckedException(t);

    }

    /**
     * 如果是著名的包裹类，从cause中获得真正异常. 其他异常则不变.
     * <p>
     * Future中使用的ExecutionException 与 反射时定义的InvocationTargetException， 真正的异常都封装在Cause中
     * <p>
     * 前面 unchecked() 使用的UncheckedException同理.
     * <p>
     * from Quasar and Tomcat's ExceptionUtils
     */
    public static Throwable unwrap(Throwable t) {
        if (t instanceof java.util.concurrent.ExecutionException
                || t instanceof java.lang.reflect.InvocationTargetException || t instanceof UncheckedException) {
            return t.getCause();
        }

        return t;
    }

    /**
     * 组合unchecked与unwrap的效果
     */
    public static RuntimeException uncheckedAndWrap(Throwable t) {

        Throwable unwrapped = unwrap(t);
        if (unwrapped instanceof RuntimeException) {
            throw (RuntimeException) unwrapped;
        }
        if (unwrapped instanceof Error) {
            throw (Error) unwrapped;
        }
        throw new UncheckedException(unwrapped);
    }

    /**
     * 将StackTrace[]转换为String, 供Logger或e.printStackTrace()外的其他地方使用.
     *
     * @see Throwables#getStackTraceAsString(Throwable)
     */
    public static String stackTraceText(Throwable throwable) {
        return Throwables.getStackTraceAsString(throwable);
    }


    /**
     * 获取异常的Root Cause.
     * <p>
     * 如无底层Cause, 则返回自身
     *
     * @see Throwables#getRootCause(Throwable)
     */
    public static Throwable getRootCause(Throwable t) {
        return Throwables.getRootCause(t);
    }


    /**
     * 拼装 短异常类名: 异常信息.
     * <p>
     * 与Throwable.toString()相比使用了短类名
     *
     * @see ExceptionUtils#getMessage(Throwable)
     */
    public static String toStringWithShortName(@Nullable Throwable t) {
        return ExceptionUtils.getMessage(t);
    }

    /**
     * 拼装 短异常类名: 异常信息 <-- RootCause的短异常类名: 异常信息
     */
    public static String toStringWithRootCause(@Nullable Throwable t) {
        if (t == null) {
            return StringUtils.EMPTY;
        }

        final String clsName = ClassUtils.getShortClassName(t, null);
        final String message = StringUtils.defaultString(t.getMessage());
        Throwable cause = getRootCause(t);

        StringBuilder sb = new StringBuilder(128).append(clsName).append(": ").append(message);
        if (cause != t) {
            sb.append("; <---").append(toStringWithShortName(cause));
        }

        return sb.toString();
    }

    /**
     * from Netty, 为静态异常设置StackTrace.
     * <p>
     * 对某些已知且经常抛出的异常, 不需要每次创建异常类并很消耗性能的并生成完整的StackTrace. 此时可使用静态声明的异常.
     * <p>
     * 如果异常可能在多个地方抛出，使用本函数设置抛出的类名和方法名.
     * <p>
     * <pre>
     * private static RuntimeException TIMEOUT_EXCEPTION = ExceptionUtil.setStackTrace(new RuntimeException("Timeout"),
     * 		MyClass.class, "mymethod");
     *
     * </pre>
     */
    public static <T extends Throwable> T setStackTrace(T exception, Class<?> throwClass, String throwClazz) {
        exception.setStackTrace(
                new StackTraceElement[]{new StackTraceElement(throwClass.getName(), throwClazz, null, -1)});
        return exception;// NOSONAR
    }


}
