package com.smartru.service.jpa.impl;

import com.smartru.entity.Task;
import com.smartru.repository.TaskRepository;
import com.smartru.service.jpa.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Qualifier("defaultTaskService")
public class TaskServiceImplDef implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Task add(Task task) {
        task = taskRepository.saveAndFlush(task);
        log.info("IN add - task: {} successfully saved", task.getTaskBody());
        return task;
    }

    @Override
    public Task update(Task task) {
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Optional<Task> optTask = taskRepository.findById(task.getId());
        if (optTask.isPresent()){
            task=taskRepository.saveAndFlush(task);
            log.info("IN update - task#{} successfully updated in database",task.getId());
            return task;
        }
        System.out.println(taskRepository.findById(task.getId()).isEmpty());
        log.warn("IN update - task#{} is absent", task.getId());
        throw new IllegalArgumentException("Task is absent");
    }
}
