package org.autumn.mybatis.mapperhandler.binding.handler.impl;

import java.lang.reflect.Method;

import org.apache.ibatis.session.SqlSession;
import org.autumn.mybatis.mapperhandler.binding.context.MapperHandlerContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.annotation.TransactionAnnotationParser;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AbstractTransactionMapperHandler extends AbstractMapperHandler implements InitializingBean, ApplicationContextAware {

    private static final TransactionAnnotationParser DEFAULT_TRANSACTION_ANNOTATION_PARSER = new SpringTransactionAnnotationParser();

    private TransactionAnnotationParser transactionAnnotationParser;

    private ApplicationContext applicationContext;

    private PlatformTransactionManager platformTransactionManager;

    public AbstractTransactionMapperHandler() {
        super();
    }

    public AbstractTransactionMapperHandler(int order) {
        super(order);
    }

    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    public TransactionAnnotationParser getTransactionAnnotationParser() {
        return transactionAnnotationParser;
    }

    public void setTransactionAnnotationParser(TransactionAnnotationParser transactionAnnotationParser) {
        this.transactionAnnotationParser = transactionAnnotationParser;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null != applicationContext && null == platformTransactionManager) {
            try {
                this.platformTransactionManager = this.applicationContext.getBean(PlatformTransactionManager.class);
            } catch (Exception e) {
                //ignore
            }
        }
    }

    /**
     * 在事务环境中执行
     *
     * @param sqlSession
     * @param context
     * @param transactionCallback
     *
     * @return
     */
    protected Object executeWithTransaction(final SqlSession sqlSession, final MapperHandlerContext context, final TransactionCallback<Object> transactionCallback) {
        if (null == this.getPlatformTransactionManager()) {
            return transactionCallback.doInTransaction(null);
        } else {
            TransactionDefinition def = this.resolveTransactionDefinition(context.getMethod());
            TransactionOperations to = new TransactionTemplate(this.getPlatformTransactionManager(), def);
            return to.execute(transactionCallback);
        }
    }

    /**
     * 解析事务定义
     *
     * @param method
     *
     * @return
     */
    private TransactionDefinition resolveTransactionDefinition(Method method) {
        TransactionAnnotationParser parser = this.getTransactionAnnotationParser();
        if (null == parser) {
            parser = DEFAULT_TRANSACTION_ANNOTATION_PARSER;
        }
        TransactionDefinition def = parser.parseTransactionAnnotation(method);
        if (null != def) {
            return def;
        } else {
            return new RuleBasedTransactionAttribute();
        }
    }
}
