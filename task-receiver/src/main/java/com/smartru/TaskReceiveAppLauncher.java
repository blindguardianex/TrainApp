package com.smartru;

import com.smartru.configuration.rabbitmq.TaskQueueRabbitStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import redis.clients.jedis.Jedis;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
public class TaskReceiveAppLauncher {

    private static ApplicationContext ctx;

    public static void main(String[] args) {
        ctx = SpringApplication.run(TaskReceiveAppLauncher.class, args);
        testRedis();
        taskRabbitQueueStarting();
        log.info("Application started!");
    }

    private static void taskRabbitQueueStarting(){
        TaskQueueRabbitStarter queueStarter = ctx.getBean(TaskQueueRabbitStarter.class);
        queueStarter.declareTaskQueue();
    }

    private static void testRedis(){
        Jedis jedis = ctx.getBean(Jedis.class);
        assert (jedis.ping().equals("PONG"));
        log.info("Jedis is ready!");
    }
}
