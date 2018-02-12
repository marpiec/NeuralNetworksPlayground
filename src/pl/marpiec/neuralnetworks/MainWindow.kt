package pl.marpiec.neuralnetworks

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import pl.marpiec.neuralnetworks.JavaFxBuilder.borderPane
import pl.marpiec.neuralnetworks.JavaFxBuilder.button
import pl.marpiec.neuralnetworks.JavaFxBuilder.scene
import pl.marpiec.neuralnetworks.JavaFxBuilder.vBox
import pl.marpiec.neuralnetworks.drivinggame.Game
import java.time.Clock

class MainWindow: Application() {

    private val clock = Clock.systemDefaultZone()

    private val viewModel = ViewModel()
    private val eventBus = EventBus()
    private val commandBus = CommandBus(viewModel, eventBus)

    private val keyboardState = KeyboardState()

    private val drivingGame = Game(Canvas(400.0, 400.0), keyboardState)


    override fun start(stage: Stage?) {
        println("Initialized")
        if(stage != null) {
            showMainWindow(stage)
        } else {
            throw IllegalStateException("Cannot handle null stage")
        }

        drivingGame.start()
    }

    private fun showMainWindow(stage: Stage) {

        val scene: Scene = scene(
                width = 600.0,
                height = 400.0,
                fillColor = Color.TRANSPARENT,
                parent = borderPane(
                        center = drivingGame.canvas,
                        left = vBox(
                                button(
                                        text = "Do something",
                                        onAction = {event -> commandBus.doSomething()}
                        )
                )
            )
        )

        scene.setOnKeyPressed{event -> when (event.code) {
            UP -> keyboardState.up = true
            DOWN -> keyboardState.down = true
            LEFT -> keyboardState.left = true
            RIGHT -> keyboardState.right = true
            else -> {}
        }}

        scene.setOnKeyReleased { when (it.code) {
            UP -> keyboardState.up = false
            DOWN -> keyboardState.down = false
            LEFT -> keyboardState.left = false
            RIGHT -> keyboardState.right = false
            else -> {}
        }}

        stage.setTitle("Neural networks!")
        stage.setScene(scene)
        stage.show()

    }
}