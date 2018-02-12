package pl.marpiec.neuralnetworks.drivinggame

import pl.marpiec.neuralnetworks.KeyboardState


class GameEngine(val model: GameModel, val keyboardState: KeyboardState) {

    var startTime: Long = 0

    fun start(now: Long): Unit {
        startTime = now
    }

    fun nextFrame(now: Long): Unit {

        val playerInput = PlayerInput(keyboardState.left, keyboardState.right, keyboardState.up, keyboardState.down)

        model.obstacles.forEach { when(it) {
            is RectangularObstacle -> updateRectangularObstacle(it, now)
        }}

        model.players.forEach {
            updatePlayer(it, playerInput)
        }

        updateCamera()
    }

    private fun updateRectangularObstacle(o: RectangularObstacle, now: Long): Unit {
//    val cycle = (now - startTime).toDouble / 1000.0 * Math.PI
        // Do nothing
    }

    private fun updatePlayer(player: Player, playerInput: PlayerInput): Unit {

        if(playerInput.accelerate) {
            player.speed = Math.min(player.speed + 0.0001, 0.01)
        }

        if(playerInput.breaking) {
            player.speed = Math.max(player.speed - 0.0001, - 0.002)
        }

        if(playerInput.turnLeft) {
            player.direction -= 0.002
        }

        if(playerInput.turnRight) {
            player.direction += 0.002
        }

        player.x += player.speed * Math.sin(player.direction)
        player.y -= player.speed * Math.cos(player.direction)

    }

    private fun updateCamera(): Unit {

        var topPlayer = model.players.first()

        model.players.forEach {
            if(it.y > topPlayer.y) {
                topPlayer = it
            }
        }

        model.camera.y = topPlayer.y - 18

    }



}