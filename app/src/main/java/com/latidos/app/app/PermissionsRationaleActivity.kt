package com.latidos.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class PermissionsRationaleActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val texto = TextView(this)
        texto.text = "Latidos necesita acceso a tu frecuencia cardíaca para mostrar tus BPM."
        texto.textSize = 20f
        texto.setPadding(40, 80, 40, 40)

        setContentView(texto)
    }
}
