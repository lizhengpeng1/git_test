package com.itlaoqi.springbootweb;

import com.itlaoqi.springbootweb.common.filter.AccessRecorderFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootwebApplication {

	//在入口类中注册Filter
	@Bean //@Bean会将方法中的返回对象在SpringBoot启动的时候放入IOC容器
	public FilterRegistrationBean filterRegiste(){
		FilterRegistrationBean regFilter = new FilterRegistrationBean();
		//创建并注册AccessRecorderFilter
		regFilter.setFilter(new AccessRecorderFilter());
		//对所有请求进行拦截
		regFilter.addUrlPatterns("/*");
		//过滤器名字
		regFilter.setName("AccessRecorder");
		//设置排序,如果系统中有多个过滤器,order就决定了那个过滤器先执行，数字越小越靠前执行
		regFilter.setOrder(1);
		return regFilter;
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringbootwebApplication.class, args);
	}
}
