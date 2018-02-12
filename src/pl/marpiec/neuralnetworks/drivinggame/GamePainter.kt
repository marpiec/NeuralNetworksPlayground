package pl.marpiec.neuralnetworks.drivinggame

class GamePainter(val gameCanvas: GameCanvas, val gameModel: GameModel) {

    fun paint(): Unit {

        gameCanvas.clearView()

        gameModel.obstacles.forEach { draw(it)}
        gameModel.players.forEach {draw(it)}
    }

    private fun draw(visibleObject: VisibleObject): Unit {

        visibleObject.viewModel().forEach { when(it) {
            is DrawableRectangle -> gameCanvas.drawRectangle(visibleObject.x + it.x, visibleObject.y + it.y, it.width, it.height, it.background, it.border, it.rotation)
        }}

    }
}
