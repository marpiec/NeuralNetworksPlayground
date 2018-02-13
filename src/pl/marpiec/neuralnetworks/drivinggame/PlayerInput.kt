package pl.marpiec.neuralnetworks.drivinggame

class PlayerInput(var turnLeft: Boolean = false,
                  var turnRight: Boolean = false,
                  var accelerate: Boolean = false,
                  var breaking: Boolean = false) {
    fun allPressed(): Boolean {
        return turnLeft && turnRight && accelerate && breaking;
    }
}