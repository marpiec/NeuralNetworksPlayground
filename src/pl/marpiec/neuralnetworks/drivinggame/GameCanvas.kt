package pl.marpiec.neuralnetworks.drivinggame

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.transform.Affine
import javafx.scene.transform.Rotate


class GameCanvas(val canvas: Canvas,
                 val camera: GameCamera) {

    private val gc = canvas.getGraphicsContext2D()


    fun clearView(): Unit {
        gc.setFill(Color.BLACK)
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)
    }

    fun drawRectangle(x: Double, y: Double, width: Double, height: Double, background: Color, border: Color, rotation: Double): Unit {
        gc.setFill(background)
        gc.setStroke(border)
        val widthRatio = canvas.width / camera.width
        val heightRatio = canvas.height / camera.width

        val viewX = (x - camera.x) * widthRatio
        val viewY = (y - camera.y) * heightRatio

        val viewWidth = width * widthRatio
        val viewHeight = height * heightRatio

//    gc.fillRect(0,0,5,5)


        if(rotation == 0.0) {
            gc.fillRect(viewX, viewY, viewWidth, viewHeight)
        } else {
            gc.save()
            gc.transform(Affine(Rotate(Math.toDegrees(rotation), viewX + viewWidth / 2.0, viewY + viewHeight / 2.0)))
            gc.fillRect(viewX, viewY, viewWidth, viewHeight)
            gc.restore()
        }

    }

}
