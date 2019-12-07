package org.yoregs.machine.example.queue

import org.yoregs.machine.ScenarioMachine

// TODO:
//  1. Исполнение
//  2. Обобщение

fun main(args: Array<String>) {
    val queueElementDef = QueueElementDef<String>()
    val emptyQueueDef = EmptyQueueDef(queueElementDef)
    val queueClientDef = HelloWorldClientDef()

    val scenarioMachine = ScenarioMachine()
        .def(queueElementDef)
        .def(emptyQueueDef)
        .def(queueClientDef)
        .build()

    scenarioMachine.execute()
}