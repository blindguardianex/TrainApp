package com.smartru.service.jpa;

import com.smartru.entity.Task;

public interface TaskService {

    Task add(Task task);
    Task update(Task task);
}
