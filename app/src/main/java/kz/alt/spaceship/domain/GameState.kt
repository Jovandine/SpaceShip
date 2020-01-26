package kz.alt.spaceship.domain

sealed class GameState {

    data class Game(
        val spaceShip: SpaceShip,
        val meteors: List<Meteor>
    ) : GameState()

    data class GameOver(
        val game: Game
    ) : GameState()

    object Start : GameState()
}