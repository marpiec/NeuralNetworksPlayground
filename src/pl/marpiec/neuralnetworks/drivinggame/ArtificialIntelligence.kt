package pl.marpiec.neuralnetworks.drivinggame

import java.lang.Math.exp

class Axon (var weight: Double,
            var input: Neuron)

fun sigmoid(input: Double): Double {
    return 1.0 / (1.0 + exp(-input))
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
            neuron.bias += (Math.random() - 0.5) * 5.0
            neuron.inputs.forEach {axon ->
                axon.weight += (Math.random() - 0.5) * 5.0
            }
            mutateNeurons(neuron.inputs.map { it.input })
        })
    }

}


class ArtificialIntelligence {

    var neuralNetwork = NeuralNetwork()

    fun init() {
        neuralNetwork = NeuralNetwork()
        neuralNetwork.mutate()
    }

    fun getInputForPlayer(player: Player): PlayerInput {

        var counter = 0
        do {
            neuralNetwork.setInputs((player.x - player.width) / 20, (20.0 - player.x - player.width) / 20)
            counter++
        } while ((neuralNetwork.getOutput().allPressed()))

        println(neuralNetwork.getOutput().accelerate.toString() + " "+neuralNetwork.getOutput().breaking)

        return neuralNetwork.getOutput()

    }

}