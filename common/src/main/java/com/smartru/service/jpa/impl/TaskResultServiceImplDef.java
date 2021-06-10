package com.smartru.service.jpa.impl;

import com.smartru.entity.TaskResult;
import com.smartru.repository.TaskResultRepository;
import com.smartru.service.jpa.TaskResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.Augmentable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("defaultTaskResultService")
public class TaskResultServiceImplDef implements TaskResultService {

    @Autowired
    private TaskResultRepository resultRepository;

    @Override
    public TaskResult add(TaskResult result) {
        result = resultRepository.saveAndFlush(result);
        log.info("IN add - task result by task #{} successfully saved", result.getTask().getId());
        return result;
    }
}