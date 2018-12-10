package org.mengyun;

import java.util.HashSet;
import java.util.Set;


import org.mengyun.tcctransaction.repository.RedisTransactionRepository;
import org.mengyun.tcctransaction.spring.recover.DefaultRecoverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class Conf {

	
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
