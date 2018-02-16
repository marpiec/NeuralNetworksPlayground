package pl.marpiec.neuralnetworks.drivinggame

import java.lang.Math.exp
import java.util.*

class Axon (var weight: Double,
            var input: Neuron)

fun sigmoid(input: Double): Double {
    return 1.0 / (1.0 + exp(-input))
}

class Neuron(var bias: Double,
             var inputs: ArrayList<Axon>,
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
                      copyNeuron(network.speed),
                      copyNeuron(network.rotation),
                      copyNeuron(network.perception3),
                      copyNeuron(network.perception4),
                      copyNeuron(network.perception5),
                      copyNeuron(network.perception6),
                      copyNeuron(network.perception7),
                      copyNeuron(network.accelerate),
                      copyNeuron(network.breaking),
                      copyNeuron(network.turnLeft),
                      copyNeuron(network.turnRight),
                      network.inputs.mapTo(arrayListOf()) {copyNeuron(it)},
                      network.outputs.mapTo(arrayListOf()) {copyNeuron(it)})
    }

    private fun copyNeuron(neuron: Neuron): Neuron {
        val cached = neurons[neuron]
        return if(cached == null) {
            val neuronCopy = Neuron(neuron.bias, neuron.inputs.mapTo(arrayListOf()) { copyAxon(it) }, neuron.notEvaluated, neuron.value)
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


class NeuralNetwork(val speed: Neuron = Neuron.empty(),
                    val rotation: Neuron = Neuron.empty(),
                    val perception3: Neuron = Neuron.empty(),
                    val perception4: Neuron = Neuron.empty(),
                    val perception5: Neuron = Neuron.empty(),
                    val perception6: Neuron = Neuron.empty(),
                    val perception7: Neuron = Neuron.empty(),
                    val accelerate: Neuron = Neuron.empty(),
                    val breaking: Neuron = Neuron.empty(),
                    val turnLeft: Neuron = Neuron.empty(),
                    val turnRight: Neuron = Neuron.empty(),
                    val inputs: ArrayList<Neuron> = arrayListOf(speed, rotation, perception3, perception4, perception5, perception6, perception7),
                    val outputs: ArrayList<Neuron> = arrayListOf(accelerate, breaking, turnLeft, turnRight)) {

    private val random = Random()

    private fun neuronsLayer(count: Int): ArrayList<Neuron> {
        return IntRange(1, count).mapTo(arrayListOf()) { Neuron(0.0, arrayListOf(), true, 0.0) }
    }

    init {

        val layerA = neuronsLayer(5)
//        val layerB = neuronsLayer(4)

        interconnectAll(inputs, layerA)
//        interconnectAll(layerA, layerB)
        interconnectAll(layerA, outputs)
    }

    private fun interconnectAll(inputs: ArrayList<Neuron>, outputs: ArrayList<Neuron>) {

        for (input in inputs) {
            for (output in outputs) {
                output.inputs.add(Axon(0.0, input))
            }
        }

    }

    fun setInputs(perception: PlayerPerception) {

        clearNeurons(outputs)

        this.speed.forceValue(perception.speed)
        this.rotation.forceValue(perception.rotation)
        this.perception3.forceValue(perception.distances[3])
        this.perception4.forceValue(perception.distances[4])
        this.perception5.forceValue(perception.distances[5])
        this.perception6.forceValue(perception.distances[6])
        this.perception7.forceValue(perception.distances[7])
    }

    private fun clearNeurons(neurons: ArrayList<Neuron>) {
       neurons.forEach {neuron ->
           neuron.value = 0.0
           neuron.notEvaluated = true;
           clearNeurons(neuron.inputs.mapTo(arrayListOf()) {it.input})
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

    private fun mutateNeurons(neurons: ArrayList<Neuron>) {
        neurons.forEach({ neuron ->
            if(neuron.notEvaluated) {
                if(Math.random() < 0.2) {
                    neuron.bias = neuron.bias + random.nextGaussian() / 5
                    neuron.inputs.forEach {axon ->
                        axon.weight = axon.weight * (1 + random.nextGaussian() / 5) + random.nextGaussian() / 5
                    }
                }
                neuron.notEvaluated = false
                mutateNeurons(neuron.inputs.mapTo(arrayListOf()) { it.input })
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


    fun mutate(players: ArrayList<Player>, generation: Int) {
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
            neuralNetworks[counter] = if(counter + survivorsCount <= players.size || true) {

                var c = survivorsCount
                val rand = Math.random() - (1.0 / (c + 1))
                while(rand > 1.0 / c) {
                    c--
                }

                NeuralNetworkCopier().copy(neuralNetworks.getValue(c)).mutate()
            } else {
                NeuralNetwork().mutate()
            }

            counter++
        }

        if(survivors.isNotEmpty()) {
            println("Generation " + generation+" mutation took " + (System.currentTimeMillis() - start)+" millis, best player " + (-survivors.first().y))
        }


    }

    fun getInputForPlayer(player: Player, perception: PlayerPerception): PlayerInput {

        val neuralNetwork = neuralNetworks.getValue(player.id)

        neuralNetwork.setInputs(perception)


        return neuralNetwork.getOutput()
    }


}