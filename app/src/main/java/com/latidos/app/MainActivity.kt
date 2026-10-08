package com.latidos.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val texto = TextView(this)
        texto.text = "Latidos ❤️"
        texto.textSize = 30f
        texto.setPadding(40, 80, 40, 40)

        setContentView(texto)
    }
}
