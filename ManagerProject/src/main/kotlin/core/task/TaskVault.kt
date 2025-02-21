package org.example.core.task

import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Component
class TaskVault {

    private val taskVault = HashMap<String, Task>()
    private val subtaskVault = HashMap<String, Subtask>()
    fun getTask(id: String): Task?{
        return taskVault[id]
    }
    fun getSubtask(id: String): Subtask?{
        return subtaskVault[id]
    }

    fun createTask(id: String, numOfWorkers: Int, maxLength: Int, hash: String): Task{
        val task = Task(id, ArrayList(), hash)
        for(workerNum in 1..numOfWorkers){
            val subtaskId = UUID.randomUUID().toString()
            val subtask = Subtask(id, subtaskId,null, hash, TaskStatus.CREATED, maxLength, numOfWorkers, workerNum)
            subtaskVault[subtaskId] = subtask
            task.subtasks.add(subtask)
        }
        taskVault[id] = task
        return task
    }
}