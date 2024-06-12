package org.javers.spring.auditable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * An interface that encapsulates the context information during the execution of an audited method.
 * <p>
 * The context information includes:
 * <ul>
 * <li>TargetClass: {@link #getTargetClass}</li>
 * <li>TargetMethod: {@link #getTargetMethod}</li>
 * <li>Target: {@link #getTarget}</li>
 * <li>TargetMethodParameters: {@link #getTargetMethodParameters}</li>
 * <li>TargetMethodArgs: {@link #getTargetMethodArgs}</li>
 * </ul>
 * <p>
 * An audited method is annotated with audit annotations defined in {@link org.javers.spring.annotation}.
 * </p>
 *
 * @author Xiangcheng Kuo
 * @see org.javers.spring.annotation
 * @see JoinPoint
 * @see MethodSignature
 * @see Method
 * @see Parameter
 * @since 2024-06-07
 */
public interface AuditingExecutionContext {

	/**
	 * Gets the target class.
	 *
	 * @return the target class.
	 */
	Class<?> getTargetClass();

	/**
	 * Gets the fully qualified name of the target class.
	 *
	 * @return the target class name.
	 */
	default String getTargetClassName() {
		return this.getTargetClass().getName();
	}

	/**
	 * Gets the target method that was audited.
	 *
	 * @return the target method.
	 */
	Method getTargetMethod();

	/**
	 * Gets the name of the target method.
	 *
	 * @return the target method name.
	 */
	default String getTargetMethodName() {
		return this.getTargetMethod().getName();
	}

	/**
	 * Gets the target that executed the audited method.
	 *
	 * @return the target object.
	 */
	Object getTarget();

	/**
	 * Gets the arguments passed to the target method.
	 *
	 * @return an array of arguments.
	 */
	Object[] getTargetMethodArgs();


	/**
	 * Gets the parameters of the target method.
	 *
	 * @return an array of parameters.
	 */
	default Parameter[] getTargetMethodParameters() {
		return this.getTargetMethod().getParameters();
	}

	/**
	 * Creates a new instance of the default implementation of {@link AuditingExecutionContext} from the given {@link JoinPoint}.
	 *
	 * @param jp the join point.
	 * @return the new instance.
	 * @throws IllegalArgumentException if the join point is null.
	 */
	static AuditingExecutionContext from(JoinPoint jp) {
		if (jp == null) {
			throw new IllegalArgumentException("JoinPoint is null");
		}

		return new JoinPointAuditingExecutionContext(jp);
	}

	/**
	 * Default implementation of the {@link AuditingExecutionContext} interface, providing the context information
	 * based on a given {@link JoinPoint}.
	 */
	class JoinPointAuditingExecutionContext implements AuditingExecutionContext {

		private final JoinPoint jp;

		private JoinPointAuditingExecutionContext(JoinPoint jp) {
			this.jp = jp;
		}

		@Override
		public Class<?> getTargetClass() {
			return AopProxyUtils.ultimateTargetClass(jp.getThis());
		}

		@Override
		public Method getTargetMethod() {
			Signature signature = jp.getSignature();
			Method targetMethod;
			if (signature instanceof MethodSignature ms) {
				targetMethod = ms.getMethod();
			} else {
				throw new IllegalArgumentException("Unsupported signature type: " + signature.getClass().getName());
			}
			return targetMethod;
		}

		@Override
		public Object getTarget() {
			return jp.getThis();
		}

		@Override
		public Object[] getTargetMethodArgs() {
			return jp.getArgs();
		}

	}

}
