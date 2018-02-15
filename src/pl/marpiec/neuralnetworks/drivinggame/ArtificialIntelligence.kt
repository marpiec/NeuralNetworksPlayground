package pl.marpiec.neuralnetworks.drivinggame

import java.lang.Math.exp
import java.util.*

class Axon (var weight: Double,
            var input: Neuron)

fun sigmoid(input: Double): Double {
    return 1.0 / (1.0 + exp(-input))
}

class Neuron(var bias: Double,
             var inputs: MutableList<Axon>,
             var notEvaluated: Boolean,
             var value: Double) {

    var inputsValue = 0.0

    companion object {
        fun empty(): Neuron {
            return Neuron(0.0, arrayListOf(), true, 0.0)
        }
    }

    fun calculateValue(): Double {
        if(notEvaluated) {
            inputsValue = inputs.map { it.weight * it.input.calculateValue()}.sum() + bias
            value = sigmoid(inputsValue)
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

    private val random = Random()

    private fun neuronsLayer(count: Int): MutableList<Neuron> {
        return IntRange(1, count).map { Neuron(0.0, mutableListOf(), true, 0.0) }.toMutableList()
    }

    init {

        val layerA = neuronsLayer(5)
//        val layerB = neuronsLayer(4)

        interconnectAll(inputs, layerA)
//        interconnectAll(layerA, layerB)
        interconnectAll(layerA, outputs)
    }

    private fun interconnectAll(inputs: MutableList<Neuron>, outputs: MutableList<Neuron>) {

        for (input in inputs) {
            for (output in outputs) {
                output.inputs.add(Axon(0.0, input))
            }
        }

    }

    fun setInputs(perception: PlayerPerception) {

        clearNeurons(outputs)

        this.speedX.forceValue(perception.speedX)
        this.speedY.forceValue(perception.speedY)
        this.leftDistance.forceValue(perception.leftDistance)
        this.rightDistance.forceValue(perception.rightDistance)
        this.frontLeftDistance.forceValue(perception.frontLeftDistance)
        this.frontRightDistance.forceValue(perception.frontRightDistance)
        this.frontLeftOrtogonalDistance.forceValue(perception.frontLeftOrtogonalDistance)
        this.frontRightOrtogonalDistance.forceValue(perception.frontRightOrtogonalDistance)
    }

    private fun clearNeurons(neurons: List<Neuron>) {
       neurons.forEach {neuron ->
           neuron.value = 0.0
           neuron.notEvaluated = true;
           clearNeurons(neuron.inputs.map {it.input})
       }
    }


    fun getOutput(): PlayerInput {

        outputs.forEach { o -> o.calculateValue() }

        return PlayerInput(turnLeft.value > 0.5,
                turnRight.value > 0.5,
                accelerate.value > 0.5,
                breaking.value > 0.5)


    }


    fun mutate(): NeuralNetwork {
        clearNeurons(outputs)
        mutateNeurons(outputs)
        return this
    }

    private fun mutateNeurons(neurons: List<Neuron>) {
        neurons.forEach({ neuron ->
            if(neuron.notEvaluated) {
                if(Math.random() < 0.2) {
                    neuron.bias = neuron.bias + random.nextGaussian() / 5
                    neuron.inputs.forEach {axon ->
                        axon.weight = axon.weight * (1 + random.nextGaussian() / 5) + random.nextGaussian() / 5
                    }
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
        val start = System.currentTimeMillis()

        val sorted = players.sortedBy { it.y }
        val survivorsCount = players.size / 5
        val survivors = sorted.take(survivorsCount)
        val rest = sorted.drop(survivorsCount)
        val survivorsWithNetworks = survivors.map { Pair(it, neuralNetworks.getValue(it.id)) }

        var counter = 1

        survivorsWithNetworks.forEach { s ->
            s.first.id = counter
            neuralNetworks[counter] = s.second
            counter++
        }

        rest.forEach { r ->
            r.id = counter
            neuralNetworks[counter] = if(counter + survivorsCount <= players.size) {
                NeuralNetworkCopier().copy(neuralNetworks.getValue(counter % survivorsCount + 1)).mutate()
            } else {
                NeuralNetwork().mutate()
            }

            counter++
        }

        if(survivors.isNotEmpty()) {
            println("Mutation took " + (System.currentTimeMillis() - start)+" millis, best player " + (-survivors.first().y))
        }


    }

    fun getInputForPlayer(player: Player, perception: PlayerPerception): PlayerInput {

        val neuralNetwork = neuralNetworks.getValue(player.id)

        neuralNetwork.setInputs(perception)


        return neuralNetwork.getOutput()
    }


}