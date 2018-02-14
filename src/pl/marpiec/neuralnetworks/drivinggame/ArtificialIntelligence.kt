package pl.marpiec.neuralnetworks.drivinggame

import java.lang.Math.exp

class Axon (var weight: Double,
            var input: Neuron)

fun sigmoid(input: Double): Double {
    return 2.0 / (1.0 + exp(-input)) - 1.0
}

class Neuron(var bias: Double,
             var inputs: ArrayList<Axon>,
             var notEvaluated: Boolean,
             var value: Double) {
    fun calculateValue(): Double {
        if(notEvaluated) {
            value = sigmoid(inputs.map { it.weight * it.input.calculateValue() }.sum() + bias)
            notEvaluated = false
        }
        return value
    }
}


class NeuralNetwork {

    val leftDistance = Neuron(0.0, arrayListOf(), true, 0.0)
    val rightDistance = Neuron(0.0, arrayListOf(), true, 0.0)

    val accelerate = Neuron(0.0, arrayListOf(), true, 0.0)
    val breaking = Neuron(0.0, arrayListOf(), true, 0.0)
    val turnLeft = Neuron(0.0, arrayListOf(), true, 0.0)
    val turnRight = Neuron(0.0, arrayListOf(), true, 0.0)

    val inputs: ArrayList<Neuron> = arrayListOf(leftDistance, rightDistance)
    val outputs: ArrayList<Neuron> = arrayListOf(accelerate, breaking, turnLeft, turnRight)

    init {
        interconnectAll(inputs, outputs)
    }

    private fun interconnectAll(inputs: List<Neuron>, outputs: List<Neuron>) {

        for (input in inputs) {
            for (output in outputs) {
                output.inputs.add(Axon(1.0, input))
            }
        }

    }

    fun setInputs(leftDistance: Double, rightDistance: Double) {

        clearNeurons(outputs)

        this.leftDistance.value = sigmoid(leftDistance + this.leftDistance.bias)
        this.leftDistance.notEvaluated = false;
        this.rightDistance.value = sigmoid(rightDistance + this.rightDistance.bias)
        this.rightDistance.notEvaluated = false;
    }

    private fun clearNeurons(neurons: List<Neuron>) {
       neurons.forEach {neuron ->
           neuron.value = 0.0
           neuron.notEvaluated = true;
           clearNeurons(neuron.inputs.map {it.input})
       }
    }


    fun getOutput(): PlayerInput {
        return PlayerInput(turnLeft.calculateValue() > turnRight.calculateValue(),
                turnLeft.calculateValue() < turnRight.calculateValue(),
                accelerate.calculateValue() > breaking.calculateValue(),
                accelerate.calculateValue() < breaking.calculateValue())
    }


    fun mutate(): Unit {
        mutateNeurons(outputs)
    }

    private fun mutateNeurons(neurons: List<Neuron>) {
        neurons.forEach({ neuron ->
            neuron.bias += (Math.random() - 0.5)
            neuron.inputs.forEach {axon ->
                axon.weight *= 1.0 + (Math.random() - 0.5) / 5
            }
            mutateNeurons(neuron.inputs.map { it.input })
        })
    }

}


class ArtificialIntelligence {

    val neuralNetworks = mutableMapOf<Int, NeuralNetwork>()

    fun init(players: Int) {
        for(p in 1..players) {
            neuralNetworks.put(p, NeuralNetwork())
        }
    }


    fun mutate() {
        neuralNetworks.forEach {it.value.mutate()}
    }

    fun getInputForPlayer(player: Player): PlayerInput {

        val neuralNetwork = neuralNetworks.getValue(player.id)
        neuralNetwork.setInputs((player.x - player.width) / 20, (20.0 - player.x - player.width) / 20)

        return neuralNetwork.getOutput()
    }


}