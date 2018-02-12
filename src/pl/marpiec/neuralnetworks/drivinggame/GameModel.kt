package pl.marpiec.neuralnetworks.drivinggame

import javafx.scene.paint.Color

interface VisibleObject {
    fun viewModel(): Iterable<Drawable>
    var x: Double
    var y: Double
}

interface Obstacle : VisibleObject

class Player(override var x: Double,
             override var y: Double,
             val width: Double,
             val length: Double,
             var direction: Double,
             var speed: Double) : VisibleObject {

    override fun viewModel() = listOf(
        DrawableRectangle(-width / 2.0, -length / 2.0, width, length, Color.BLUE, Color.GREEN, direction)
    )
}


class RectangularObstacle(override var x: Double,
                          override var y: Double,
                          var width: Double,
                          var height: Double) : Obstacle {

    override fun viewModel() = listOf(
        DrawableRectangle(-width / 2.0, -height / 2.0, width, height, Color.GREEN, Color.BLUE, 0.0)
    )

}

class GameCamera(var x: Double,
                 var y: Double,
                 var width: Double,
                 var height: Double)


class GameModel(var trackWidth: Double,
                val players: MutableList<Player>,
                val obstacles: MutableList<Obstacle>,
                var camera: GameCamera) {

    companion object {
        fun empty() = GameModel(20.0, mutableListOf(), mutableListOf(), GameCamera(0.0, 0.0, 20.0, 20.0))
    }


    fun addObstacle (obstacle: Obstacle): Unit {
        obstacles += obstacle
    }

    fun addPlayer (player: Player): Unit {
        players += player
    }

}

