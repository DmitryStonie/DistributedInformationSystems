package org.example.core.task

import org.springframework.stereotype.Component

@Component
class TaskVault {

    private val vault = HashMap<String, Task>()
    fun addTask(id: String, task: Task){
        vault[id] = task
    }
    fun getTask(id: String): Task?{
        return vault[id]
    }

    fun createTask(id: String, numOfWorkers: Int): Task{
        val statuses = ArrayList(generateSequence { TaskStatus.IN_PROGRESS }.take(numOfWorkers).toList())
        val task = Task(id, statuses, ArrayList())
        this.addTask(id, task)
        return task
    }
}