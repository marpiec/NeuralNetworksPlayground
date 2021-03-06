package pl.marpiec.neuralnetworks.drivinggame


class GameEngine(val model: GameModel,
                 val artificialIntelligence: ArtificialIntelligence,
                 val onGameEnd: () -> Unit) {

    var startTime: Long = 0
    var frame: Long = 0

    val segments = 8

    fun nextFrame(delta: Long): Unit {

        model.players.filter { !it.crashed }.forEach {player ->


            val distances: Array<Double> = arrayOf(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                                                   Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE) // 8 directions, clockwise

            model.obstacles.forEach { obstacle ->

                val xDiff = obstacle.x - player.x
                val yDiff = obstacle.y - player.y
                val distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff)

                val bucket: Int = if(yDiff != 0.0) {
                    val arcTan = Math.toDegrees(Math.atan2(xDiff, yDiff) - player.rotation)
                    val b1 = ((arcTan + 360 / 16) + 360 * 10) / (360 / 8)
                    b1.toInt() % 8
                } else {
                    if(xDiff > 0) {
                        2
                    } else {
                        6
                    }
                }

                if(distance < distances[bucket]) {
                    distances[bucket] = distance;
                }
            }


            val perception = PlayerPerception(player.speed, player.rotation, distances)

            player.perception = perception

//            println("${player.id} ${perception}")

            val playerInput = artificialIntelligence.getInputForPlayer(player, perception)
            updatePlayer(player, playerInput, delta)
        }

        model.obstacles.forEach { when(it) {
            is RectangularObstacle -> updateRectangularObstacle(it)
        }}

        updateCamera()

        detectCollisions()

        frame+= delta

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

    private fun updateRectangularObstacle(o: RectangularObstacle): Unit {
//    val cycle = (now - startTime).toDouble / 1000.0 * Math.PI
        // Do nothing
    }

    private fun updatePlayer(player: Player, playerInput: PlayerInput, delta: Long): Unit {

        val maxSpeedY = 0.01

        if(playerInput.accelerate) {
            player.speed = Math.max(player.speed - 0.00001 * delta, -maxSpeedY)
        }

        if(playerInput.breaking) {
           player.speed = Math.min(player.speed + 0.0001 * delta, 0.0)
        }

//            if(!playerInput.accelerate && !playerInput.breaking && player.speedY < 0) {
//            player.speedY = Math.min(player.speedY + 0.00002* delta, 0.0)
//            }

        if(playerInput.turnLeft) {
            player.rotation = (player.rotation - 0.005* delta + Math.PI * 2) % (Math.PI * 2)
        }

        if(playerInput.turnRight) {
            player.rotation = (player.rotation + 0.005* delta + Math.PI * 2) % (Math.PI * 2)
        }


        player.x -= player.speed * Math.sin(player.rotation) * delta
        player.y += player.speed * Math.cos(player.rotation) * delta

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