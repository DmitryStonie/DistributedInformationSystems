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

}