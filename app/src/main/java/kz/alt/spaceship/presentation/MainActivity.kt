package kz.alt.spaceship.presentation

import android.app.Activity
import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.coroutines.*
import kz.alt.spaceship.R
import kz.alt.spaceship.domain.GameDrawer
import kz.alt.spaceship.domain.GameLogic
import kz.alt.spaceship.domain.GameState
import kz.alt.spaceship.domain.GameStatePresenter
import kotlin.coroutines.CoroutineContext

private const val LOOP_DELAY_MS: Long = 32

class MainActivity : Activity(),
    GameStatePresenter,
    CoroutineScope
{

    private val uiContext = Dispatchers.Main
    override val coroutineContext: CoroutineContext
        get() = uiContext

    private val gameLogic: GameLogic = GameLogic(
        displayDencity = Resources.getSystem().displayMetrics.density
    )
    private lateinit var gameDrawer: GameDrawer

    private var loopJob: Job = Job()

    private lateinit var leftButton: View
    private lateinit var rightButton: View
    private lateinit var contentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        initViews()
        contentLayout.post {
            gameLogic.init(
                presenter = this,
                displayWidth = contentLayout.width,
                displayHeight = contentLayout.height
            )
            gameDrawer = contentLayout as ViewGameDrawer
            startGame()
        }
    }

    override fun onGameStateUpdate(gameState: GameState) {
        when (gameState) {
            is GameState.Start -> {}
            is GameState.GameOver -> {
                handleGame(gameState.game)
                showGameOver()
                stopGame()
            }
            is GameState.Game -> {
                handleGame(gameState)
            }
        }
    }

    private fun startGame() {
        gameLogic.start()
        startLoop()
    }

    private fun stopGame() {
        stopLoop()
    }

    private fun handleGame(gameState: GameState.Game) {
        gameDrawer.drawGame(gameState)
    }

    private fun initViews() {
        leftButton = findViewById(R.id.left_button)
        leftButton.setOnTouchListener(MoveSpaceShipUpTouchListener())
        rightButton = findViewById(R.id.right_button)
        rightButton.setOnTouchListener(MoveSpaceShipDownTouchListener())
        contentLayout = findViewById(R.id.content_layout)
    }

    private fun startLoop() {
        loopJob.cancel()
        loopJob = launch {
            while (true) {
                delay(LOOP_DELAY_MS)

                gameLogic.cycle()
            }
        }
    }

    private fun stopLoop() {
        loopJob.cancel()
    }

    private fun showGameOver() {
        AlertDialog.Builder(this)
            .setTitle("Вы проиграли! Начать заново?")
            .setPositiveButton("Да") { _, _ ->
                startGame()
            }
            .setNegativeButton("Нет") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    internal inner class MoveSpaceShipUpTouchListener : View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> gameLogic.onStartMovingSpaceShipUp()
                MotionEvent.ACTION_UP -> gameLogic.onStopMovingSpaceShipUp()
            }

            return true
        }
    }

    internal inner class MoveSpaceShipDownTouchListener : View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> gameLogic.onStartMovingSpaceShipDown()
                MotionEvent.ACTION_UP -> gameLogic.onStopMovingSpaceShipDown()
            }

            return true
        }
    }
}