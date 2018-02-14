package pl.marpiec.neuralnetworks.drivinggame

import javafx.scene.paint.Color

class Rectangle(val x: Double,
                val y: Double,
                val width: Double,
                val height: Double)

interface VisibleObject {
    fun viewModel(): Iterable<Drawable>
    var x: Double
    var y: Double
}

interface Obstacle : VisibleObject {
    fun toRectangle(): Rectangle
}

class PlayerPerception(val leftDistance: Double, val rightDistance: Double, val frontLeftDistance: Double, val frontRightDistance: Double)

class Player(val id: Int,
             override var x: Double,
             override var y: Double,
             val width: Double,
             val length: Double,
             var speedX: Double,
             var speedY: Double,
             var crashed: Boolean) : VisibleObject {

    override fun viewModel() = listOf(
        DrawableRectangle(-width / 2.0, -length / 2.0, width, length, Color.BLUE, if(crashed) Color.RED else Color.GREEN, 0.0)
    )

    fun toRectangle() = Rectangle(x-width / 2.0, y-length/ 2.0, width, length)
}


class RectangularObstacle(override var x: Double,
                          override var y: Double,
                          var width: Double,
                          var height: Double) : Obstacle {

    override fun viewModel() = listOf(
        DrawableRectangle(-width / 2.0, -height / 2.0, width, height, Color.GREEN, Color.BLUE, 0.0)
    )

    override fun toRectangle() = Rectangle(x-width / 2.0, y-height/ 2.0, width, height)
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
        fun empty(): GameModel {
            val trackWidth = 20.0
            return GameModel(trackWidth, mutableListOf(), mutableListOf(), GameCamera(0.0, 0.0, trackWidth, trackWidth))
        }
    }


    fun addObstacle (obstacle: Obstacle): Unit {
        obstacles += obstacle
    }

    fun addPlayer (player: Player): Unit {
        players += player
    }

    fun clear() {
        players.clear()
        obstacles.clear()
    }

}

