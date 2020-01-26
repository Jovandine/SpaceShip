package kz.alt.spaceship.domain

interface GameStatePresenter {

    fun onGameStateUpdate(gameState: GameState)
}