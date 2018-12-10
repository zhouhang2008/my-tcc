package org.mengyun;

import org.mengyun.tcctransaction.recover.TransactionRecovery;
import org.mengyun.tcctransaction.spring.ConfigurableCoordinatorAspect;
import org.mengyun.tcctransaction.spring.ConfigurableTransactionAspect;
import org.mengyun.tcctransaction.spring.recover.RecoverScheduledJob;
import org.mengyun.tcctransaction.spring.support.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.support.SpringTransactionConfigurator;
import org.quartz.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@SpringBootApplication
public class AppStarter {

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

	public static void main(String[] args) {
		SpringApplication.run(AppStarter.class, args);
	}

}
