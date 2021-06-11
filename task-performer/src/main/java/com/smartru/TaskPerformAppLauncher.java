package com.smartru;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Приложение, отвечающее за аутентификацию/авторизацию пользователя
 * и прием задач от него
 */
@Slf4j
@SpringBootApplication
@EnableJpaAuditing
public class TaskPerformAppLauncher {

    private static ApplicationContext ctx;

    public static void main(String[] args){
        ctx = SpringApplication.run(TaskPerformAppLauncher.class, args);
        log.info("Application started!");
    }
}
