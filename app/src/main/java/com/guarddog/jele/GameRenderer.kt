package com.guarddog.jele

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import java.util.Random
import kotlin.math.abs
import kotlin.math.floor

class GameRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var player: Player
    private lateinit var ground: Ground
    private val coins = mutableListOf<Coin>()
    private val obstacles = mutableListOf<Obstacle>()
    private val random = Random()

    private var coinValue = 0
    private var hudCoins: TextView? = null
    private var hudDistance: TextView? = null
    private var overlayStart: View? = null
    private var overlayGameOver: View? = null
    private var tvFinalScore: TextView? = null

    // sound & vibration
    private var soundManager: SoundManager? = null
    private val vibrator: Vibrator? = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    // skins
    private val skinColors = arrayOf(
        floatArrayOf(0.22f, 0.70f, 0.95f, 1f), // starter
        floatArrayOf(0.96f, 0.72f, 0.22f, 1f), // orange
        floatArrayOf(0.63f, 0.86f, 0.33f, 1f)  // green
    )
    private val skinPrices = intArrayOf(0, 15, 30)
    private var ownedMask = 1
    private var currentSkin = 0

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)
    private var lastTime = System.nanoTime()

    private val cameraPosition = floatArrayOf(0f, 5.5f, -11f)
    private val cameraLookAt = floatArrayOf(0f, 0f, 10f)

    private var started = false
    private var isGameOver = false
    private var distance = 0f

    fun bindHud(coinsView: TextView, distanceView: TextView) {
        hudCoins = coinsView
        hudDistance = distanceView
        updateHud()
    }

    fun bindOverlays(startOverlay: View, gameOverOverlay: View, finalScore: TextView) {
        this.overlayStart = startOverlay
        this.overlayGameOver = gameOverOverlay
        this.tvFinalScore = finalScore
    }

    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.08f, 0.14f, 0.22f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        player = Player()
        ground = Ground()

        // init sound manager
        soundManager = SoundManager(context)

        // load saved progress
        coinValue = SaveManager.loadCoins(context)
        currentSkin = SaveManager.loadSkin(context)
        ownedMask = SaveManager.loadOwned(context)
        applySkin(currentSkin)

        spawnCoins()
        spawnObstacles()
        isGameOver = false
    }

    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.perspectiveM(projectionMatrix, 0, 62f, ratio, 0.1f, 200f)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        val now = System.nanoTime()
        val delta = (now - lastTime) / 1_000_000_000f
        lastTime = now

        update(delta)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, cameraPosition[0], cameraPosition[1], cameraPosition[2], cameraLookAt[0], cameraLookAt[1], cameraLookAt[2], 0f, 1f, 0f)
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        ground.draw(vpMatrix)
        coins.filter { it.isAlive }.forEach { it.draw(vpMatrix) }
        obstacles.filter { it.isAlive }.forEach { it.draw(vpMatrix) }
        player.draw(vpMatrix)
    }

    private fun update(delta: Float) {
        if (!started || isGameOver) return

        player.update(delta)

        // update distance + HUD
        distance = player.position[2]
        updateHud()

        for (coin in coins) {
            if (!coin.isAlive) continue
            coin.update(delta)

            val dx = abs(coin.position[0] - player.position[0])
            val dy = abs(coin.position[1] - player.position[1])
            val dz = abs(coin.position[2] - player.position[2])

            if (dx < 0.9f && dy < 1.2f && dz < 1.2f) {
                coin.isAlive = false
                coinValue += 1
                SaveManager.saveCoins(context, coinValue)
                updateHud()

                // play coin / jele sound and vibrate shortly
                soundManager?.playJele()
                vibrateShort()
            }
        }

        for (obs in obstacles) {
            if (!obs.isAlive) continue
            obs.update(delta)
            val dx = abs(obs.position[0] - player.position[0])
            val dy = abs(obs.position[1] - player.position[1])
            val dz = abs(obs.position[2] - player.position[2])
            if (dx < 0.9f && dy < 1.2f && dz < 1.2f) {
                // collision -> game over
                isGameOver = true
                // play game over sound
                soundManager?.playJele()
                vibrateShort()
                showGameOver()
            }
        }

        // Spawn more coins/obstacles ahead
        val farthestNeeded = player.position[2] + 80f
        if (coins.none { it.position[2] > farthestNeeded && it.isAlive } && coins.count { it.isAlive } < 24) {
            spawnCoins()
        }
        if (obstacles.none { it.position[2] > farthestNeeded && it.isAlive } && obstacles.count { it.isAlive } < 8) {
            spawnObstacles()
        }

        val camZ = player.position[2] - 11f
        cameraPosition[2] = camZ
        cameraLookAt[2] = player.position[2] + 6f
        player.rotationY += 110f * delta
    }

    private fun vibrateShort() {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(40)
                }
            }
        } catch (_: Exception) { }
    }

    private fun spawnCoins() {
        for (i in 0..11) {
            val lane = random.nextInt(3) - 1
            val x = lane * 3.2f
            val z = player.position[2] + 20f + i * 11f
            val coin = Coin(x, 1.2f, z)
            coins.add(coin)
        }
    }

    private fun spawnObstacles() {
        for (i in 0..3) {
            val lane = random.nextInt(3) - 1
            val x = lane * 3.2f
            val z = player.position[2] + 30f + i * 22f
            val obs = Obstacle(x, 0.9f, z)
            obstacles.add(obs)
        }
    }

    private fun updateHud() {
        hudCoins?.post { hudCoins?.text = "Coins: $coinValue" }
        val dist = floor(distance).toInt()
        hudDistance?.post { hudDistance?.text = "Distance: ${dist}m" }
    }

    fun onMoveLeft() { if (!isGameOver) player.moveLeft() }
    fun onMoveRight() { if (!isGameOver) player.moveRight() }
    fun onJump() { if (!isGameOver) player.jump(); soundManager?.playJele(); vibrateShort() }

    fun startGame() {
        started = true
        isGameOver = false
        // little start sound
        soundManager?.playJele()
    }

    fun restart() {
        // reset state
        coins.clear()
        obstacles.clear()
        coinValue = SaveManager.loadCoins(context)
        distance = 0f
        player = Player()
        applySkin(currentSkin)
        spawnCoins()
        spawnObstacles()
        isGameOver = false
        started = true
        overlayGameOver?.post { overlayGameOver?.visibility = View.GONE }
        updateHud()
    }

    private fun showGameOver() {
        tvFinalScore?.post {
            tvFinalScore?.text = "Coins: $coinValue  Distance: ${floor(distance).toInt()}m"
        }
        overlayGameOver?.post { overlayGameOver?.visibility = View.VISIBLE }
    }

    // Skin/shop API
    fun buySkin(index: Int): Boolean {
        if (index < 0 || index >= skinPrices.size) return false
        val owned = ((ownedMask shr index) and 1) == 1
        if (owned) {
            currentSkin = index
            applySkin(index)
            SaveManager.saveSkin(context, index)
            return true
        }

        val price = skinPrices[index]
        if (coinValue >= price) {
            coinValue -= price
            ownedMask = ownedMask or (1 shl index)
            currentSkin = index
            applySkin(index)
            SaveManager.saveCoins(context, coinValue)
            SaveManager.saveOwned(context, ownedMask)
            SaveManager.saveSkin(context, index)
            updateHud()
            return true
        }
        return false
    }

    fun applySkin(index: Int) {
        val safe = index.coerceIn(0, skinColors.size - 1)
        currentSkin = safe
        player.setColor(skinColors[safe])
    }
}
