package pl.marpiec.neuralnetworks.drivinggame

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import pl.marpiec.neuralnetworks.KeyboardState


class Game(val canvas: Canvas,
           val keyboardState: KeyboardState) {

    private val players = 60

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

        artificialIntelligence.init(players)
        initGame()
        startTime = System.currentTimeMillis()
        engine.startTime = startTime
        lastFrameTime = startTime


//        for(i in 1..100000) {
//            val currentTime = System.currentTimeMillis()
//            nextFrame(currentTime, 1)
//            lastFrameTime = currentTime
//        }
//        println("Initialization in " + (System.currentTimeMillis() - startTime))


        animationTimer.start()



    }


    private fun initGame(): Unit {


        artificialIntelligence.mutate(model.players)

        val obs: List<Double> = listOf(5.0, 13.0, 2.0 ,18.5, 8.0, 16.0, 4.0, 5.0, 11.0, 1.5, 16.0, 4.0, 2.0,18.5, 15.0, 14.0, 4.0, 2.0, 10.0, 5.0, 15.0, 7.0,18.5, 12.0, 7.0, 13.0, 11.0)

        model.clear()

        obs.forEachIndexed { index, o ->
            model.addObstacle(RectangularObstacle(o, (-index * 2 - 5).toDouble(), 2.0, 1.0))
        }

        for (p in 1..players) {
            model.addPlayer(Player(p, 7.0, 0.0, 0.6, 1.4, 0.0, 0.0, false))
        }

    }


    private fun nextFrame(currentTime: Long, timeDelta: Long): Unit {
        var i = 0
        while(i < timeDelta) {
            engine.nextFrame(currentTime, 1)
            i += 1
        }
        painter.paint()
    }



}
