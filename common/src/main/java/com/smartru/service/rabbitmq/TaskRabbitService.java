package com.smartru.service.rabbitmq;

import com.smartru.entity.Task;

public interface TaskRabbitService {

    void sendTask(Task task);
}
