package pl.marpiec.neuralnetworks.drivinggame


class GameEngine(val model: GameModel,
                 val artificialIntelligence: ArtificialIntelligence,
                 val onGameEnd: () -> Unit) {

    var startTime: Long = 0
    var frame: Long = 0

    fun nextFrame(now: Long): Unit {



        model.players.filter { !it.crashed }.forEach {player ->


            val distanceLeft = distance(player, - player.width / 2)
            val distanceRight = distance(player, player.width / 2)

            val perception = PlayerPerception((player.x - player.width) / 20, (20.0 - player.x - player.width) / 20, distanceLeft, distanceRight)

            val playerInput = artificialIntelligence.getInputForPlayer(player, perception)
            updatePlayer(player, playerInput)
        }

        model.obstacles.forEach { when(it) {
            is RectangularObstacle -> updateRectangularObstacle(it, now)
        }}

        updateCamera()

        detectCollisions()

        frame++

        if(model.players.all { it.crashed } || frame > 4000) {
            onGameEnd()
            frame = 0
        }

    }

    private fun distance(player: Player, shift: Double): Double {
        val obstacleDistance = model.obstacles.filter { obstacle ->
            val rectangle = obstacle.toRectangle()
            rectangle.y + rectangle.height < player.y && rectangle.x < player.x + shift && rectangle.x + rectangle.width > player.x + shift;
        }.sortedBy { -it.y }.map { player.y - it.y }.firstOrNull()

        return if (obstacleDistance == null) {
            10000.0
        } else {
            obstacleDistance
        }
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
//            player.speedY = Math.max(player.speedY - 0.00005, -0.01)
            player.speedY = -.01
        }

        if(playerInput.breaking) {
            player.speedY = 0.0
//            player.speedY = Math.min(player.speedY + 0.0001, 0.0)
        }

        if(!playerInput.accelerate && !playerInput.breaking && player.speedY < 0) {
            player.speedY = 0.0
//            player.speedY = Math.min(player.speedY + 0.00002, 0.0)
        }

        if(playerInput.turnLeft) {
            player.speedX = -.01
//            player.speedX = Math.max(player.speedX - 0.00005, -0.01)
        }

        if(playerInput.turnRight) {
            player.speedX = .01
//            player.speedX = Math.min(player.speedX + 0.00005, 0.01)
        }

        if(!playerInput.turnLeft && !playerInput.turnRight) {
            player.speedX = 0.0;
//            if(player.speedX > 0) {
//                player.speedX = Math.max(player.speedX - 0.00002, 0.0)
//            } else if (player.speedX < 0) {
//                player.speedX = Math.min(player.speedX + 0.00002, 0.0)
//            }
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
            if(it.y < topPlayer.y) {
                topPlayer = it
            }
        }

        model.camera.y = topPlayer.y - 5

    }



}