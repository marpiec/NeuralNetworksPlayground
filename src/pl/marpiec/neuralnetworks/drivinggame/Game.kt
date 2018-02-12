package pl.marpiec.neuralnetworks.drivinggame

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import pl.marpiec.neuralnetworks.KeyboardState


class Game(val canvas: Canvas,
           val keyboardState: KeyboardState) {


    private val model: GameModel = GameModel.empty()
    private val engine = GameEngine(model, keyboardState)
    private val gameCanvas = GameCanvas(canvas, model.camera)
    private val painter = GamePainter(gameCanvas, model)

    private var startTime: Long = 0
    private var lastFrameTime: Long = 0

    private val animationTimer = object : AnimationTimer() {
        override fun handle(now: Long): Unit {
            val currentTime = System.currentTimeMillis()
            nextFrame(currentTime, currentTime - lastFrameTime)
            lastFrameTime = currentTime
        }
    }

    fun start(): Unit {

        initGame()
        startTime = System.currentTimeMillis()
        engine.startTime = startTime
        lastFrameTime = startTime
        animationTimer.start()

    }


    private fun initGame(): Unit {

        model.addObstacle(RectangularObstacle(5.0, -5.0, 2.0, 1.0))

        model.addObstacle(RectangularObstacle(10.0, -10.0, 3.0, 2.0))

        model.addObstacle(RectangularObstacle(15.0, -15.0, 3.0, 1.0))

        model.addPlayer(Player(10.0, 0.0, 1.0, 2.0, 0.0, 0.0))

    }


    private fun nextFrame(currentTime: Long, timeDelta: Long): Unit {
        var i = 0
        while(i < timeDelta) {
            engine.nextFrame(currentTime)
            i += 1
        }
        painter.paint()
    }



}
