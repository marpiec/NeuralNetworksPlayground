package pl.marpiec.neuralnetworks

class EventBus {

//    private var callbacks: Map[ClassTag[_ <: ApplicationEvent], List[_ <: ApplicationEvent => Unit]] = Map.empty
//
//    def on[T <: ApplicationEvent:ClassTag](callback: T => Unit): Unit = {
//        val clazz = classTag[T]
//        val listenersOfType = callbacks.getOrElse(clazz, {
//            val list: List[Any => Unit] = List.empty
//            callbacks += clazz -> list
//            list
//        })
//        callbacks += clazz -> (callback.asInstanceOf[ApplicationEvent => Unit] :: listenersOfType)
//    }
//
//    def broadcast[T <: ApplicationEvent:ClassTag](event: T): Unit = {
//        val clazz = classTag[T]
//        callbacks.getOrElse(clazz, List.empty).foreach(callback => callback.apply(event))
//    }

}
