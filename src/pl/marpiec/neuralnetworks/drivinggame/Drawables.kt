package pl.marpiec.neuralnetworks.drivinggame

import javafx.scene.paint.Color

interface Drawable

class DrawableRectangle(var x: Double,
                        var y: Double,
                        var width: Double,
                        var height: Double,
                        var border: Color,
                        var background: Color,
                        var rotation: Double) : Drawable
