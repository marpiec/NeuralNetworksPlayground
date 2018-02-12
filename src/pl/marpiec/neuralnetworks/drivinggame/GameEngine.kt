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

        model.players.filter { !it.crashed }.forEach {
            updatePlayer(it, playerInput)
        }

        updateCamera()

        detectCollisions()
    }

    private fun detectCollisions() {

        model.players.filter { !it.crashed }.forEach {player ->
            val playerRectangle = player.toRectangle()

            val crashedOnWall = playerRectangle.x < 0.0 || playerRectangle.x + playerRectangle.width > 20.0
            if(crashedOnWall) {
                player.crashed = true
            } else {
                val collisionDetected = model.obstacles.find { collides(it.toRectangle(), playerRectangle) } != null
                if(collisionDetected) {
                    player.crashed = true
                }
            }

        }

    }

    private fun updateRectangularObstacle(o: RectangularObstacle, now: Long): Unit {
//    val cycle = (now - startTime).toDouble / 1000.0 * Math.PI
        // Do nothing
    }

    private fun updatePlayer(player: Player, playerInput: PlayerInput): Unit {

        if(playerInput.accelerate) {
            player.speedY = Math.max(player.speedY - 0.0001, -0.01)
        }

        if(playerInput.breaking) {
            player.speedY = Math.min(player.speedY + 0.0001, 0.0)
        }

        if(!playerInput.accelerate && !playerInput.breaking && player.speedY < 0) {
            player.speedY = Math.min(player.speedY + 0.0001, 0.0)
        }

        if(playerInput.turnLeft) {
            player.speedX = Math.max(player.speedX - 0.0001, -0.01)
        }

        if(playerInput.turnRight) {
            player.speedX = Math.min(player.speedX + 0.0001, 0.01)
        }

        if(!playerInput.turnLeft && !playerInput.turnRight) {
            if(player.speedX > 0) {
                player.speedX = Math.max(player.speedX - 0.0001, 0.0)
            } else if (player.speedX < 0) {
                player.speedX = Math.min(player.speedX + 0.0001, 0.0)
            }
        }

        player.x += player.speedX
        player.y += player.speedY

    }

    private fun collides(a: Rectangle, b: Rectangle): Boolean {
        return a.x < b.x + b.width &&
            a.x + a.width > b.x &&
            a.y < b.y + b.height &&
            a.height + a.y > b.y
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