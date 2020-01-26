package kz.alt.spaceship.domain

import kotlin.random.Random

class GameLogic(
    private val displayDencity: Float
) {

    private val SPACE_SHIP_TOUCH_SPEED = 4.toPx
    private val SPACE_SHIP_WIDTH = 90.toPx
    private val SPACE_SHIP_HEIGHT = 34.toPx
    private val SPACE_SHIP_START_X = 32.toPx
    private val METEOR_WIDTH = 150.toPx
    private val METEOR_HEIGHT = 40.toPx
    private val METEOR_START_SPEED = 5.toPx
    private val METEOR_INCREASE_SPEED = 3.toPx

    private var displayWidth = 0
    private var displayHeight = 0
    private var gameState: GameState = GameState.Start
    private var isSpaceShipMovingUp: Boolean = false
    private var isSpaceShipMovingDown: Boolean = false
    private lateinit var presenter: GameStatePresenter

    fun init(
        presenter: GameStatePresenter,
        displayWidth: Int,
        displayHeight: Int
    ) {
        this.presenter = presenter
        this.displayWidth = displayWidth
        this.displayHeight = displayHeight
    }

    fun start() {
        gameState = GameState.Game(
            spaceShip = makSpaceShip(),
            meteors = makeMeteors()
        )
        updateGameState()
    }

    fun cycle() {
        val gameState = gameState
        if (gameState !is GameState.Game) return

        moveSpaceShip()
        moveMeteors()
        if (isAnyMeteorHit()) {
            this.gameState = GameState.GameOver(gameState)
        }
        updateGameState()
    }

    fun onStartMovingSpaceShipUp() {
        isSpaceShipMovingUp = true
    }

    fun onStopMovingSpaceShipUp() {
        isSpaceShipMovingUp = false
    }

    fun onStartMovingSpaceShipDown() {
        isSpaceShipMovingDown = true
    }

    fun onStopMovingSpaceShipDown() {
        isSpaceShipMovingDown = false
    }

    private fun moveSpaceShip() {
        val gameState = gameState
        if (gameState !is GameState.Game) return

        val spaceShip = gameState.spaceShip
        if (isSpaceShipMovingDown) {
            spaceShip.yPos += SPACE_SHIP_TOUCH_SPEED
            if (spaceShip.yPos + spaceShip.height >= displayHeight) {
                spaceShip.yPos = displayHeight - spaceShip.height
            }
        }
        if (isSpaceShipMovingUp) {
            spaceShip.yPos -= SPACE_SHIP_TOUCH_SPEED
            if (spaceShip.yPos <= 0) spaceShip.yPos = 0
        }
    }

    private fun updateGameState() {
        presenter.onGameStateUpdate(gameState)
    }

    private fun makSpaceShip(): SpaceShip = SpaceShip(
        width = SPACE_SHIP_WIDTH,
        height = SPACE_SHIP_HEIGHT,
        xPos = SPACE_SHIP_START_X,
        yPos = displayHeight / 2 - SPACE_SHIP_HEIGHT / 2
    )

    private fun makeMeteors(): List<Meteor> {
        val meteors = mutableListOf<Meteor>()
        repeat(2) {
            meteors.add(
                Meteor(
                    xPos = displayWidth,
                    yPos = getMeteorStartY(),
                    height = METEOR_HEIGHT,
                    width = METEOR_WIDTH,
                    isVisible = true,
                    speed = Random.nextInt(METEOR_START_SPEED, METEOR_START_SPEED * 2)
                )
            )
        }

        return meteors
    }

    private fun moveMeteorToStart(
        meteor: Meteor
    ) {
        meteor.xPos = displayWidth
        meteor.yPos = getMeteorStartY()
    }

    private fun getMeteorStartY(): Int = Random.nextInt(
        from = METEOR_HEIGHT,
        until = displayHeight - METEOR_HEIGHT
    )

    private fun moveMeteors() {
        val gameState = gameState
        if (gameState !is GameState.Game) return

        gameState.meteors.forEach {
            moveMeteor(
                meteor = it
            )
        }
    }

    private fun moveMeteor(
        meteor: Meteor
    ) {
        meteor.xPos -= meteor.speed
        val rightMeteorSide = meteor.xPos + meteor.width
        if (rightMeteorSide < 0) {
            meteor.speed += Random.nextInt(METEOR_INCREASE_SPEED)
            moveMeteorToStart(meteor)
        }
    }

    private fun isAnyMeteorHit(): Boolean {
        val gameState = gameState
        if (gameState !is GameState.Game) return false

        return gameState.meteors.any {
            isMeteorHit(gameState.spaceShip, it)
        }
    }

    private fun isMeteorHit(
        spaceShip: SpaceShip,
        meteor: Meteor
    ): Boolean {
        val rightSpaceShipSide = spaceShip.xPos + spaceShip.width
        val leftSpaceShipSide = spaceShip.xPos
        val topSpaceShipSide = spaceShip.yPos
        val bottomSpaceShipSide = spaceShip.yPos + spaceShip.height

        val rightMeteorSide = meteor.xPos + meteor.width
        val leftMeteorSide = meteor.xPos
        val topMeteorSide = meteor.yPos
        val bottomMeteorSide = meteor.yPos + meteor.height

        val isMeteorHitByX = leftMeteorSide in (leftSpaceShipSide..rightSpaceShipSide)
                || rightMeteorSide in (leftSpaceShipSide..rightSpaceShipSide)
        val isMeteorHitByY = topMeteorSide in (topSpaceShipSide..bottomSpaceShipSide)
                || bottomMeteorSide in (topSpaceShipSide..bottomSpaceShipSide)

        return isMeteorHitByX && isMeteorHitByY
    }

    private val Int.toPx: Int
        get() = (this * displayDencity).toInt()
}