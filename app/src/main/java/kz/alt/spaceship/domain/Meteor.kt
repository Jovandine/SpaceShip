package kz.alt.spaceship.domain

data class Meteor(
    val width: Int,
    val height: Int,
    var speed: Int,
    var xPos: Int,
    var yPos: Int,
    var isVisible: Boolean
)