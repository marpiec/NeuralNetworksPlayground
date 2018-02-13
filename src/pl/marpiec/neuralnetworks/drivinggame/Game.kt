package pl.marpiec.neuralnetworks.drivinggame

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import pl.marpiec.neuralnetworks.KeyboardState


class Game(val canvas: Canvas,
           val keyboardState: KeyboardState) {


    private val model: GameModel = GameModel.empty()
    private val artificialIntelligence = ArtificialIntelligence()
    private val engine = GameEngine(model, artificialIntelligence, {
        initGame()
    })
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

        artificialIntelligence.init()

        val obs: List<Double> = listOf(5.0, 13.0, 7.0, 4.0, 11.0, 16.0, 4.0, 15.0, 14.0, 4.0, 10.0, 5.0, 15.0, 7.0, 12.0, 7.0, 13.0, 11.0)

        model.clear()

        obs.forEachIndexed { index, o ->
            model.addObstacle(RectangularObstacle(o, (-index * 7 - 5).toDouble(), 8.0, 1.0))
        }

        model.addPlayer(Player(1, 7.0, 0.0, 1.0, 2.0, 0.0, 0.0, false))

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
