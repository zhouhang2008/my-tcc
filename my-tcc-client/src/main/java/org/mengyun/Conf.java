package org.mengyun;

import java.util.HashSet;
import java.util.Set;

import org.mengyun.tcctransaction.recover.TransactionRecovery;
import org.mengyun.tcctransaction.repository.RedisTransactionRepository;
import org.mengyun.tcctransaction.spring.ConfigurableCoordinatorAspect;
import org.mengyun.tcctransaction.spring.ConfigurableTransactionAspect;
import org.mengyun.tcctransaction.spring.recover.DefaultRecoverConfig;
import org.mengyun.tcctransaction.spring.recover.RecoverScheduledJob;
import org.mengyun.tcctransaction.spring.support.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.support.SpringTransactionConfigurator;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class Conf {
	
	@Bean("springBeanFactory")
	public SpringBeanFactory getSpringBeanFactory() {
		SpringBeanFactory bean = new SpringBeanFactory();
		return bean;
	}

	@Bean(name = "transactionConfigurator", initMethod = "init")
	public SpringTransactionConfigurator getTransactionConfigurator() {
		return new SpringTransactionConfigurator();
	}

	@Bean(name = "compensableTransactionAspect", initMethod = "init")
	public ConfigurableTransactionAspect getCompensableTransactionAspect(
			SpringTransactionConfigurator transactionConfigurator) {
		ConfigurableTransactionAspect bean = new ConfigurableTransactionAspect();
		bean.setTransactionConfigurator(transactionConfigurator);
		return bean;
	}

	@Bean(name = "resourceCoordinatorAspect", initMethod = "init")
	public ConfigurableCoordinatorAspect getResourceCoordinatorAspect(
			SpringTransactionConfigurator transactionConfigurator) {
		ConfigurableCoordinatorAspect bean = new ConfigurableCoordinatorAspect();
		bean.setTransactionConfigurator(transactionConfigurator);
		return bean;
	}

	@Bean(name = "transactionRecovery")
	public TransactionRecovery getTransactionRecovery(SpringTransactionConfigurator transactionConfigurator) {
		TransactionRecovery bean = new TransactionRecovery();
		bean.setTransactionConfigurator(transactionConfigurator);
		return bean;
	}

	@Bean(name = "recoverScheduler")
	public SchedulerFactoryBean getRecoverScheduler() {
		return new SchedulerFactoryBean();
	}

	@Bean(name = "recoverScheduledJob", initMethod = "init")
	public RecoverScheduledJob getRecoverScheduledJob(TransactionRecovery transactionRecovery,
			SpringTransactionConfigurator transactionConfigurator, Scheduler recoverScheduler) {
		RecoverScheduledJob bean = new RecoverScheduledJob();
		bean.setTransactionRecovery(transactionRecovery);
		bean.setTransactionConfigurator(transactionConfigurator);
		bean.setScheduler(recoverScheduler);
		return bean;
	}

	
	@Bean(name = "jedisPool")
	public JedisPool getDataSource() {
		JedisPoolConfig conf = new JedisPoolConfig();
		conf.setMaxTotal(1000);
		conf.setMaxWaitMillis(1000);
		JedisPool bean = new JedisPool(conf,"127.0.0.1",6379,1000,"123456");
		return bean;
	}
	
	@Bean("transactionRepository")
	public RedisTransactionRepository getTransactionRepository(JedisPool jedisPool) {
		RedisTransactionRepository bean = new RedisTransactionRepository();
		bean.setKeyPrefix("tcc_ut_");
		bean.setJedisPool(jedisPool);
		return bean;
	}
	
	@Bean
	public DefaultRecoverConfig getDefaultRecoverConfig() {
		DefaultRecoverConfig bean = new DefaultRecoverConfig();
		bean.setMaxRetryCount(30);
		bean.setRecoverDuration(120);
		bean.setCronExpression("0 */1 * * * ?");
		Set<Class<? extends Exception>> set = new HashSet<>();
		set.add(NoSuchMethodException.class);
		bean.setDelayCancelExceptions(set);
		return bean;
	}
}
