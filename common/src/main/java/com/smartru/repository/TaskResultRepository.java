package com.smartru.repository;

import com.smartru.entity.TaskResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskResultRepository extends JpaRepository<TaskResult,Long> {

}
