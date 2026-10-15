package com.guarddog.jele

import android.content.Context

object SaveManager {
    private const val PREFS = "jele_prefs"
    private const val KEY_COINS = "coins"
    private const val KEY_SKIN = "skin"

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
}
