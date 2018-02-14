package pl.marpiec.neuralnetworks.drivinggame


class GameEngine(val model: GameModel,
                 val artificialIntelligence: ArtificialIntelligence,
                 val onGameEnd: () -> Unit) {

    var startTime: Long = 0
    var frame: Long = 0

    fun nextFrame(now: Long, delta: Long): Unit {



        model.players.filter { !it.crashed }.forEach {player ->


            val distanceLeft = frontDistance(player.x - player.width / 2, player.y - player.length / 2)
            val distanceRight = frontDistance(player.x + player.width / 2, player.y - player.length / 2)

            val distanceLeftLeft = frontDistance(player.x - player.width * 2, player.y)
            val distanceRightRight = frontDistance(player.x + player.width * 2, player.y)

//            val ortogonalDistance = ortogonalDistance(player.)

            val perception = PlayerPerception(player.speedX, player.speedY, (player.x - player.width), (20.0 - player.x - player.width), distanceLeft, distanceRight, distanceLeftLeft, distanceRightRight)


//            println("${player.id} ${perception}")

            val playerInput = artificialIntelligence.getInputForPlayer(player, perception)
            updatePlayer(player, playerInput, delta)
        }

        model.obstacles.forEach { when(it) {
            is RectangularObstacle -> updateRectangularObstacle(it, now)
        }}

        updateCamera()

        detectCollisions()

        frame++

        if(model.players.all { it.crashed } || frame > 5000) {
            onGameEnd()
            frame = 0
        }

    }

    private fun frontDistance(x: Double, y: Double): Double {
        val inFront = model.obstacles.filter { obstacle ->
            val rectangle = obstacle.toRectangle()
            rectangle.y + rectangle.height <= y && rectangle.x <= x && rectangle.x + rectangle.width >= x;
        }
        var obstacle: Obstacle? = null

        inFront.forEach {
            if(obstacle == null || it.y > obstacle!!.y) {
                obstacle = it
            }
        }

        return if(obstacle == null) {
            10000.0
        } else {
            val rect = obstacle!!.toRectangle()
            y - rect.y + rect.width
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

    private fun updatePlayer(player: Player, playerInput: PlayerInput, delta: Long): Unit {

        val inertion = true

        if(inertion) {
            if(playerInput.accelerate) {
                player.speedY = Math.max(player.speedY - 0.00003 * delta, -0.01)
            }

            if(playerInput.breaking) {
            player.speedY = Math.min(player.speedY + 0.0001* delta, 0.0)
            }

//            if(!playerInput.accelerate && !playerInput.breaking && player.speedY < 0) {
//            player.speedY = Math.min(player.speedY + 0.00002* delta, 0.0)
//            }

            if(playerInput.turnLeft) {
            player.speedX = Math.max(player.speedX - 0.0002* delta, -0.01)
            }

            if(playerInput.turnRight) {
            player.speedX = Math.min(player.speedX + 0.0002* delta, 0.01)
            }

            if(!playerInput.turnLeft && !playerInput.turnRight) {
                if(player.speedX > 0) {
                    player.speedX = Math.max(player.speedX - 0.00002* delta, 0.0)
                } else if (player.speedX < 0) {
                    player.speedX = Math.min(player.speedX + 0.00002* delta, 0.0)
                }
            }
        } else {
            if(playerInput.accelerate) {
                player.speedY = Math.max(player.speedY - 0.00005 * delta, -0.01)
//                player.speedY = -.01
            }

            if(playerInput.breaking) {
//                player.speedY = 0.0
                player.speedY = Math.min(player.speedY + 0.0001* delta, 0.0)
            }

//            if(!playerInput.accelerate && !playerInput.breaking) {
//                player.speedY = 0.0
//                player.speedY = Math.min(player.speedY + 0.00002* delta, 0.0)
//            }

            if(playerInput.turnLeft) {
                player.speedX = -.01
            }

            if(playerInput.turnRight) {
                player.speedX = .01
            }

            if(!playerInput.turnLeft && !playerInput.turnRight) {
                player.speedX = 0.0;
            }
        }

        player.x += player.speedX * delta
        player.y += player.speedY * delta

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

        model.camera.y = topPlayer.y - 9

    }



}