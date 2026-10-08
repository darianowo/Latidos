package com.latidos.app

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord

class MainActivity : ComponentActivity() {

    private lateinit var estado: TextView

    private val permisos = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class)
    )

    private val solicitarPermisos =
        registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { permisosConcedidos ->

            if (permisosConcedidos.containsAll(permisos)) {
                estado.text = "✅ Permiso concedido\nYa podemos leer tus latidos."
            } else {
                estado.text = "❌ No se concedió el permiso."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 80, 40, 40)

        val titulo = TextView(this)
        titulo.text = "Latidos ❤️"
        titulo.textSize = 30f

        estado = TextView(this)
        estado.textSize = 18f
        estado.setPadding(0, 40, 0, 40)

        val boton = Button(this)
        boton.text = "Comprobar Health Connect"

        boton.setOnClickListener {
            comprobarHealthConnect()
        }

        layout.addView(titulo)
        layout.addView(estado)
        layout.addView(boton)

        setContentView(layout)

        comprobarHealthConnect()
    }

    private fun comprobarHealthConnect() {

        val disponibilidad = HealthConnectClient.getSdkStatus(this)

        when (disponibilidad) {

            HealthConnectClient.SDK_AVAILABLE -> {
                estado.text =
                    "🟢 Health Connect está disponible.\n\nPulsa el botón para solicitar permiso."

                solicitarPermisos.launch(permisos)
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                estado.text =
                    "🟡 Health Connect necesita actualizarse."
            }

            HealthConnectClient.SDK_UNAVAILABLE -> {
                estado.text =
                    "🔴 Health Connect no está disponible en este teléfono."
            }

            else -> {
                estado.text =
                    "⚠️ Estado desconocido de Health Connect."
            }
        }
    }
}
