package org.javers.spring.auditable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * An interface used by {@link AdvancedCommitPropertiesProvider}. This interface
 * encapsulates the execution context of an audited method
 * called by the {@link JaversSpringDataAuditable} aspect.
 * <br/><br/>
 *
 * The execution context includes:
 * <ul>
 * <li>An audited class &mdash; {@link #getTargetClass()}</li>
 * <li>An audited method &mdash; {@link #getTargetMethod()}</li>
 * <li>AspectJ JoinPoint: {@link #getJoinPoint()}</li>
 * <li>TargetMethodArgs: {@link #getTargetMethodArgs()}</li>
 * </ul>
 *
 * @author Xiangcheng Kuo
 * @see AdvancedCommitPropertiesProvider
 * @see JoinPoint
 * @see Method
 * @since 7.5
 */
public interface AuditedMethodExecutionContext {

	/**
	 * Gets an audited class. Most likely a {@link CrudRepository}.
	 */
	Class<?> getTargetClass();

	/**
	 * Gets an audited method of a {@link CrudRepository}.
	 */
	Method getTargetMethod();

	/**
	 * An underlying AspectJ {@link JoinPoint}
	 */
	public JoinPoint getJoinPoint();

	/**
	 * Gets arguments passed to an audited method.
	 */
	Object[] getTargetMethodArgs();

	/**
	 * Gets the fully qualified name of a target class.
	 */
	default String getTargetClassName() {
		return this.getTargetClass().getName();
	}

	/**
	 * Gets the name of an audited method.
	 */
	default String getTargetMethodName() {
		return this.getTargetMethod().getName();
	}

	/**
	 * Gets parameters of an audited method.
	 */
	default Parameter[] getTargetMethodParameters() {
		return this.getTargetMethod().getParameters();
	}

	/**
	 * Creates a new instance of the default implementation of {@link AuditedMethodExecutionContext} from a given {@link JoinPoint}.
	 *
	 * @throws IllegalArgumentException if the join point is null.
	 */
	static AuditedMethodExecutionContext from(JoinPoint jp) {
		if (jp == null) {
			throw new IllegalArgumentException("JoinPoint is null");
		}

		return new JoinPointAuditingExecutionContext(jp);
	}

	/**
	 * Default implementation of the {@link AuditedMethodExecutionContext} interface, providing the context information
	 * based on a given {@link JoinPoint}.
	 */
	class JoinPointAuditingExecutionContext implements AuditedMethodExecutionContext {

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

		/**
		 * An underlying aspectj {@link JoinPoint}
		 */
		@Override
		public JoinPoint getJoinPoint() {
			return jp;
		}

		@Override
		public Object[] getTargetMethodArgs() {
			return jp.getArgs();
		}

	}

}
