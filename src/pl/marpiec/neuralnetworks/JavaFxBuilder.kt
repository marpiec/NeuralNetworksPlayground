package pl.marpiec.neuralnetworks

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import java.awt.image.BufferedImage

class ChangeEvent<T>(val observable: ObservableValue<out T>, val oldValue: T, val newValue: T)

object JavaFxBuilder {

    fun scene(parent: Parent, width: Double, height: Double, fillColor: Color? = null, onKeyPressed: ((KeyEvent) -> Unit)? = null): Scene {
        val scene = Scene(parent, width, height)
        if(fillColor != null) scene.setFill(fillColor)
        if(onKeyPressed != null) {
            scene.onKeyPressed = EventHandler<KeyEvent> { event -> onKeyPressed(event) }
        }
        return scene
    }


    fun borderPane(center: Node? = null, top: Node? = null, right: Node? = null, bottom: Node? = null, left: Node? = null): BorderPane {
        val pane = BorderPane()
        if(center != null) pane.setCenter(center)
        if(top != null) pane.setTop(top)
        if(right != null) pane.setRight(right)
        if(bottom != null) pane.setBottom(bottom)
        if(left != null) pane.setLeft(left)
        return pane
    }

    fun stackPane(style: String? = null, vararg children: Node): StackPane {
        val stackPane = StackPane(*children)
        if(style != null) stackPane.setStyle(style)
        return stackPane
    }

    fun Pane(style: String? = null, onMouseClicked: ((MouseEvent) -> Unit)? = null, vararg children: Node): Pane {
        val pane = Pane(*children)
        if(style != null) pane.setStyle(style)
        if(onMouseClicked != null) {
            pane.onMouseClicked = EventHandler<MouseEvent> { event -> onMouseClicked(event) }
        }

        pane.setFocusTraversable(true)
        return pane
    }



    fun button(text: String,
               onAction: ((ActionEvent) -> Unit)? = null): Button {
        val btn = Button()
        btn.text = text
        if(onAction != null) {
            btn.onAction = EventHandler<ActionEvent> {event -> onAction(event)}
        }
        return btn
    }

    fun <T>listView(items: ObservableList<T> = FXCollections.observableArrayList<T>()): ListView<T> {
        return ListView<T>(items)
    }

    fun textArea(text: String, onChange: ((ChangeEvent<String>) -> Unit)? = null, onFocusChange: ((ChangeEvent<Boolean>) -> Unit)? = null): TextArea {
        val textArea = TextArea(text)
        textArea.setPrefColumnCount(50)
        textArea.setPrefRowCount(20)
        textArea.setWrapText(false)
        if(onFocusChange != null) {

            textArea.focusedProperty().addListener {observable: ObservableValue<out Boolean>, oldValue: Boolean, newValue: Boolean -> onFocusChange(ChangeEvent(observable, oldValue, newValue))}
        }
        if(onChange != null) {
            textArea.textProperty().addListener {observable: ObservableValue<out String>, oldValue: String, newValue: String -> onChange(ChangeEvent(observable, oldValue, newValue))}
        }
        return textArea
    }

    fun canvas(width: Double, height: Double): Canvas {
        return Canvas(width, height)
    }

    fun rectangle(x: Double, y: Double, width: Double, height: Double, fill: Color? = null,
    onMousePressed: ((MouseEvent) -> Unit)? = null,
    onDragged: ((MouseEvent) -> Unit)? = null): Rectangle {
        val rectange = Rectangle(x, y, width, height)
        if(fill!=null) rectange.setFill(fill)
        if(onMousePressed != null) {
            rectange.setOnMousePressed { event -> onMousePressed(event) }
        }
        if(onDragged != null) {
            rectange.setOnMouseDragged { event -> onDragged(event) }
        }
        return rectange
    }

    fun Circle(centerX: Double, centerY: Double, radius: Double, id: String? = null, fill: Color? = null,
    onDragDetected: ((MouseEvent) -> Unit)? = null, onDragged: ((MouseEvent) -> Unit)? = null): Circle {
        val circle = Circle(radius)
        if(id != null) circle.setId(id)
        if(fill!=null) circle.setFill(fill)
        if(onDragDetected != null) {
            circle.setOnDragDetected(EventHandler<MouseEvent> { event -> onDragDetected(event) })
        }
        if(onDragged != null) {
            circle.setOnMouseDragged(EventHandler<MouseEvent> { event -> onDragged(event) })
        }
        return circle
    }

    fun imageView(image: BufferedImage, x: Int, y: Int): ImageView {
        val imageView = ImageView(SwingFXUtils.toFXImage(image, null))
        imageView.x = x.toDouble()
        imageView.y = y.toDouble()
        return imageView
    }

    fun vBox(vararg children: Node): VBox {
        return VBox(*children)
    }

}
