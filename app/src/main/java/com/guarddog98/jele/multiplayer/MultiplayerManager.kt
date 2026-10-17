package com.guarddog98.jele.multiplayer

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * MultiplayerManager handles anonymous Firebase auth, presence, writing local player state,
 * listening for other players in the same room, and throttling updates to avoid excessive usage.
 *
 * Usage:
 *  val mm = MultiplayerManager(roomId = "room1")
 *  mm.onConnected = { id -> /* store my id */ }
 *  mm.onPlayersUpdate = { players -> /* update rendering */ }
 *  mm.start()
 *  mm.updateLocalState(x, y)
 *  mm.leaveRoom() // when leaving
 */
class MultiplayerManager(
    private val roomId: String,
    private val updateRateMs: Long = 100L // default: 10 updates/sec
) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val playersRef: DatabaseReference = db.child("rooms").child(roomId).child("players")

    private var myPlayerId: String = ""
    private var myRef: DatabaseReference? = null

    private var roomListener: ValueEventListener? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastSentAt = 0L
    private var pendingState: PlayerState? = null

    // Callbacks
    var onConnected: ((myId: String) -> Unit)? = null
    var onPlayersUpdate: ((players: Map<String, PlayerState>) -> Unit)? = null
    var onError: ((Exception) -> Unit)? = null

    fun start() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    myPlayerId = auth.currentUser?.uid ?: "p_${Random.nextInt(1_000_000)}"
                    myRef = playersRef.child(myPlayerId)

                    // ensure removal on disconnect
                    myRef?.onDisconnect()?.removeValue()

                    // write initial state
                    val initial = PlayerState(id = myPlayerId, x = 0f, y = 0f, lastUpdated = System.currentTimeMillis())
                    myRef?.setValue(initial)?.addOnFailureListener { e -> onError?.invoke(e) }

                    // listen for players in the room
                    roomListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val map = mutableMapOf<String, PlayerState>()
                            for (child in snapshot.children) {
                                try {
                                    val p = child.getValue(PlayerState::class.java)
                                    if (p != null) map[p.id] = p
                                } catch (ex: Exception) {
                                    Log.w("MP", "Failed to parse player state", ex)
                                }
                            }
                            onPlayersUpdate?.invoke(map)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            onError?.invoke(error.toException())
                        }
                    }
                    playersRef.addValueEventListener(roomListener as ValueEventListener)

                    onConnected?.invoke(myPlayerId)
                } else {
                    task.exception?.let { onError?.invoke(it) }
                }
            }
    }

    fun updateLocalState(x: Float, y: Float, heading: Float = 0f) {
        val now = System.currentTimeMillis()
        pendingState = PlayerState(id = myPlayerId, x = x, y = y, heading = heading, lastUpdated = now)

        if (now - lastSentAt >= updateRateMs) {
            sendPendingState()
        } else {
            coroutineScope.launch {
                val wait = updateRateMs - (now - lastSentAt)
                if (wait > 0) delay(wait)
                sendPendingState()
            }
        }
    }

    private fun sendPendingState() {
        val toSend = pendingState ?: return
        lastSentAt = System.currentTimeMillis()
        myRef?.setValue(toSend)?.addOnFailureListener { e -> onError?.invoke(e) }
    }

    fun leaveRoom() {
        try { myRef?.removeValue() } catch (_: Exception) {}
        roomListener?.let { playersRef.removeEventListener(it) }
        coroutineScope.cancel()
    }

    fun destroy() {
        leaveRoom()
        try { auth.signOut() } catch (_: Exception) {}
    }
}
