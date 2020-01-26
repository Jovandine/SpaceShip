package kz.alt.spaceship.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kz.alt.spaceship.R
import kz.alt.spaceship.domain.GameDrawer
import kz.alt.spaceship.domain.GameState
import kz.alt.spaceship.domain.Meteor
import kz.alt.spaceship.domain.SpaceShip

class ViewGameDrawer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
        GameDrawer
{
    private val spaceShipDrawable: Drawable = ContextCompat.getDrawable(context,
        R.drawable.ic_space_ship
    )!!
    private val meteorDrawable: Drawable = ContextCompat.getDrawable(context,
        R.drawable.ic_meteor
    )!!
    private var gameState: GameState = GameState.Start

    init {
        setBackgroundResource(R.drawable.bg_main)
    }

    override fun onDraw(canvas: Canvas) {
        val gameState = this.gameState as? GameState.Game ?: return

        drawSpaceShip(canvas, gameState.spaceShip)
        drawMeteors(canvas, gameState.meteors)
    }

    override fun drawGame(gameState: GameState) {
        this.gameState = gameState
        invalidate()
    }

    private fun drawSpaceShip(
        canvas: Canvas,
        spaceShip: SpaceShip
    ) {
        val drawable = spaceShipDrawable

        drawable.setBounds(
            spaceShip.xPos,
            spaceShip.yPos,
            spaceShip.xPos + spaceShip.width,
            spaceShip.yPos + spaceShip.height
        )
        drawable.draw(canvas)
    }

    private fun drawMeteors(
        canvas: Canvas,
        meteors: List<Meteor>
    ) {
        meteors.forEach {
            drawMeteor(canvas, it)
        }
    }

    private fun drawMeteor(
        canvas: Canvas,
        meteor: Meteor
    ) {
        val drawable = meteorDrawable

        drawable.setBounds(
            meteor.xPos,
            meteor.yPos,
            meteor.xPos + meteor.width,
            meteor.yPos + meteor.height
        )
        drawable.draw(canvas)
    }
}