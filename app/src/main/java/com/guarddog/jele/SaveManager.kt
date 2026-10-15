package com.guarddog.jele

import android.content.Context

object SaveManager {
    private const val PREFS = "jele_prefs"
    private const val KEY_COINS = "coins"
    private const val KEY_SKIN = "skin"
    private const val KEY_OWNED = "owned"

    fun saveCoins(ctx: Context, coins: Int) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit().putInt(KEY_COINS, coins).apply()
    }

    fun loadCoins(ctx: Context): Int {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getInt(KEY_COINS, 0)
    }

    fun saveSkin(ctx: Context, skinIndex: Int) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit().putInt(KEY_SKIN, skinIndex).apply()
    }

    fun loadSkin(ctx: Context): Int {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getInt(KEY_SKIN, 0)
    }

    fun saveOwned(ctx: Context, ownedMask: Int) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit().putInt(KEY_OWNED, ownedMask).apply()
    }

    fun loadOwned(ctx: Context): Int {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getInt(KEY_OWNED, 1) // default owned mask = 1 (starter skin)
    }
}
