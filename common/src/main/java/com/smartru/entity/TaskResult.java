package com.smartru.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Entity
@Table(name = "task_results")
@NoArgsConstructor
@AllArgsConstructor
public class TaskResult extends BaseEntity{

    @Getter
    @NotNull
    @JsonIgnore
    @OneToOne(targetEntity = Task.class,
            cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "task")
    private Task task;

    @NotBlank
    @Column(name = "result_body")
    private String result;

    public String get(){
        return result;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "result='" + result + '\'' +
                '}';
    }
}
