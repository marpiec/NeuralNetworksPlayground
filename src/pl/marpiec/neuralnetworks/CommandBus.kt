package pl.marpiec.neuralnetworks


class CommandBus(val model: ViewModel,
                 val eventBus: EventBus) {
    fun doSomething(): Unit {
        println("Do something")
    }

}