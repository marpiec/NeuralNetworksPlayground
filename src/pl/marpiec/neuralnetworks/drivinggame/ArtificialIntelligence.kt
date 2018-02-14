package pl.marpiec.neuralnetworks.drivinggame

import java.lang.Math.exp

class Axon (var weight: Double,
            var input: Neuron)

fun sigmoid(input: Double): Double {
    return 2.0 / (1.0 + exp(-input)) - 1.0
}

class Neuron(var bias: Double,
             var inputs: MutableList<Axon>,
             var notEvaluated: Boolean,
             var value: Double) {

    companion object {
        fun empty(): Neuron {
            return Neuron(0.0, arrayListOf(), true, 0.0)
        }
    }

    fun calculateValue(): Double {
        if(notEvaluated) {
            value = sigmoid(inputs.map { it.weight * it.input.calculateValue() }.sum() + bias)
            notEvaluated = false
        }
        return value
    }

    fun forceValue(value: Double) {
        this.value = value
        this.notEvaluated = false
    }
}

class NeuralNetworkCopier {

    private val axons: MutableMap<Axon, Axon> = mutableMapOf()
    private val neurons: MutableMap<Neuron, Neuron> = mutableMapOf()

    fun copy(network: NeuralNetwork): NeuralNetwork {
        return NeuralNetwork(
                      copyNeuron(network.speedX),
                      copyNeuron(network.speedY),
                      copyNeuron(network.leftDistance),
                      copyNeuron(network.rightDistance),
                      copyNeuron(network.frontLeftDistance),
                      copyNeuron(network.frontRightDistance),
                      copyNeuron(network.frontLeftOrtogonalDistance),
                      copyNeuron(network.frontRightOrtogonalDistance),
                      copyNeuron(network.accelerate),
                      copyNeuron(network.breaking),
                      copyNeuron(network.turnLeft),
                      copyNeuron(network.turnRight),
                      network.inputs.map {copyNeuron(it)}.toMutableList(),
                      network.outputs.map {copyNeuron(it)}.toMutableList())
    }

    private fun copyNeuron(neuron: Neuron): Neuron {
        val cached = neurons[neuron]
        return if(cached == null) {
            val neuronCopy = Neuron(neuron.bias, neuron.inputs.map { copyAxon(it) }.toMutableList(), neuron.notEvaluated, neuron.value)
            neurons[neuron] = neuronCopy
            return neuronCopy
        } else {
            cached
        }
    }

    private fun copyAxon(axon: Axon): Axon {
        val cached = axons[axon]
        return if(cached == null) {
            val axonCopy = Axon(axon.weight, copyNeuron(axon.input))
            axons[axon] = axonCopy
            return axonCopy
        } else {
            cached
        }
    }

}


class NeuralNetwork(val speedX: Neuron = Neuron.empty(),
                    val speedY: Neuron = Neuron.empty(),
                    val leftDistance: Neuron = Neuron.empty(),
                    val rightDistance: Neuron = Neuron.empty(),
                    val frontLeftDistance: Neuron = Neuron.empty(),
                    val frontRightDistance: Neuron = Neuron.empty(),
                    val frontLeftOrtogonalDistance: Neuron = Neuron.empty(),
                    val frontRightOrtogonalDistance: Neuron = Neuron.empty(),
                    val accelerate: Neuron = Neuron.empty(),
                    val breaking: Neuron = Neuron.empty(),
                    val turnLeft: Neuron = Neuron.empty(),
                    val turnRight: Neuron = Neuron.empty(),
                    val inputs: MutableList<Neuron> = arrayListOf(speedX, speedY, leftDistance, rightDistance, frontLeftDistance, frontRightDistance, frontLeftOrtogonalDistance, frontRightOrtogonalDistance),
                    val outputs: MutableList<Neuron> = arrayListOf(accelerate, breaking, turnLeft, turnRight)) {


    private fun neuronsLayer(count: Int): MutableList<Neuron> {
        return IntRange(1, count).map { Neuron(0.0, mutableListOf(), true, 0.0) }.toMutableList()
    }

    init {

//        val layerA = neuronsLayer(5)
//        val layerB = neuronsLayer(4)

//        interconnectAll(inputs, layerA)
//        interconnectAll(layerA, layerB)
        interconnectAll(inputs, outputs)
    }

    private fun interconnectAll(inputs: MutableList<Neuron>, outputs: MutableList<Neuron>) {

        for (input in inputs) {
            for (output in outputs) {
                output.inputs.add(Axon(1.0, input))
            }
        }

    }

    fun setInputs(perception: PlayerPerception) {

        clearNeurons(outputs)

        this.speedX.forceValue(sigmoid(perception.speedX + this.speedX.bias))
        this.speedY.forceValue(sigmoid(perception.speedY + this.speedY.bias))
        this.leftDistance.forceValue(sigmoid(perception.leftDistance + this.leftDistance.bias))
        this.rightDistance.forceValue(sigmoid(perception.rightDistance + this.rightDistance.bias))
        this.frontLeftDistance.forceValue(sigmoid(perception.frontLeftDistance + this.frontLeftDistance.bias))
        this.frontRightDistance.forceValue(sigmoid(perception.frontRightDistance + this.frontRightDistance.bias))
        this.frontLeftOrtogonalDistance.forceValue(sigmoid(perception.frontLeftOrtogonalDistance + this.frontLeftOrtogonalDistance.bias))
        this.frontRightOrtogonalDistance.forceValue(sigmoid(perception.frontRightOrtogonalDistance + this.frontRightOrtogonalDistance.bias))
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
        clearNeurons(outputs)
        mutateNeurons(outputs)
    }

    private fun mutateNeurons(neurons: List<Neuron>) {
        neurons.forEach({ neuron ->
            if(neuron.notEvaluated) {
                neuron.bias += neuron.bias * (1.0 + (Math.random() - 0.5) / 5) + (Math.random() - 0.5) / 2
                neuron.inputs.forEach {axon ->
                    axon.weight *= 1.0 + (Math.random() - 0.5) / 10
                }
                neuron.notEvaluated = false
                mutateNeurons(neuron.inputs.map { it.input })
            }
        })
    }

}


class ArtificialIntelligence {

    val neuralNetworks = mutableMapOf<Int, NeuralNetwork>()

    fun init(players: Int) {
        for(p in 1..players) {
            val neuralNetwork = NeuralNetwork()
            neuralNetwork.mutate()
            neuralNetworks[p] = neuralNetwork
        }
    }


    fun mutate(players: List<Player>) {

        val sorted = players.sortedBy { it.y }
        val batchSize = players.size / 10
        val survivors = sorted.take(batchSize)
        var rest = sorted.drop(batchSize)
//        println("---")

        for(i in 2..10) {
            val taken = rest.take(batchSize)
            rest = rest.drop(batchSize)

            survivors.zip(taken).forEach { (survivor, t) ->
                val network = NeuralNetworkCopier().copy(neuralNetworks.getValue(survivor.id))
                network.mutate()
                neuralNetworks[t.id] = network
//                println("${survivor.id} -> ${t.id}")
            }

        }

    }

    fun getInputForPlayer(player: Player, perception: PlayerPerception): PlayerInput {

        val neuralNetwork = neuralNetworks.getValue(player.id)

        neuralNetwork.setInputs(perception)


        return neuralNetwork.getOutput()
    }


}