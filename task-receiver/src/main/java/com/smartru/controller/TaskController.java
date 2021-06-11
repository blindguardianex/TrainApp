package com.smartru.controller;

import com.smartru.entity.Task;
import com.smartru.service.jpa.TaskService;
import com.smartru.service.jpa.UserService;
import com.smartru.service.rabbitmq.TaskRabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping(value = "/api/task/",
        consumes="application/json")
public class TaskController {

    private final UserService userService;
    private final TaskService taskService;
    private final TaskRabbitService taskRabbitService;

    @Autowired
    public TaskController(UserService userService, TaskService taskService, TaskRabbitService taskRabbitService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskRabbitService = taskRabbitService;
    }

    @PostMapping("add")
    public ResponseEntity addTask(@RequestBody Task task){
        task = taskService.add(task);
        taskRabbitService.sendTask(task);
        return new ResponseEntity(HttpStatus.OK);
    }
}
