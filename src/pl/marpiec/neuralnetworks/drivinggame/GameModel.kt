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

data class PlayerPerception(val speedX: Double, val speedY: Double, val distances: Array<Double>) {
    companion object {
        fun empty(): PlayerPerception {
            return PlayerPerception(0.0, 0.0, arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
        }
    }
}

class Player(var id: Int,
             override var x: Double,
             override var y: Double,
             val width: Double,
             val length: Double,
             var speedX: Double,
             var speedY: Double,
             var crashed: Boolean,
             var perception: PlayerPerception
             ) : VisibleObject {

    override fun viewModel(): Iterable<Drawable> {
        val player = listOf(
                DrawableRectangle(-width / 2.0, -length / 2.0, width, length, Color.BLUE, if (crashed) Color.RED else Color.GREEN, 0.0)
        )

        val p = perception.distances.mapIndexed { index, distance ->
            DrawableRectangle(Math.sin(Math.PI * 2.0 / 8.0 * index) * distance,  Math.cos(Math.PI * 2.0 / 8.0 * index) * distance, 0.2, 0.2, Color.YELLOW, Color.YELLOW, 0.0)
        }

        return p.plus(player)
    }


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
                val players: ArrayList<Player>,
                val obstacles: ArrayList<Obstacle>,
                var camera: GameCamera) {

    companion object {
        fun empty(): GameModel {
            val trackWidth = 20.0
            return GameModel(trackWidth, arrayListOf(), arrayListOf(), GameCamera(0.0, 0.0, trackWidth, trackWidth))
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

