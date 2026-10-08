package com.latidos.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord

class MainActivity : Activity() {

    private lateinit var healthConnectClient: HealthConnectClient

    private val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class)
    )

    private val requestPermissionsLauncher =
        registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { grantedPermissions ->

            if (grantedPermissions.containsAll(permissions)) {
                textoEstado.text = "✅ Permiso concedido"
            } else {
                textoEstado.text = "❌ Permiso no concedido"
            }
        }

    private lateinit var textoEstado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        healthConnectClient = HealthConnectClient.getOrCreate(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 80, 40, 40)

        val titulo = TextView(this)
        titulo.text = "Latidos ❤️"
        titulo.textSize = 30f

        textoEstado = TextView(this)
        textoEstado.text = "Necesitamos permiso para leer tu frecuencia cardíaca."
        textoEstado.textSize = 18f
        textoEstado.setPadding(0, 40, 0, 40)

        val boton = Button(this)
        boton.text = "Dar permiso a Health Connect"

        boton.setOnClickListener {
            solicitarPermiso()
        }

        layout.addView(titulo)
        layout.addView(textoEstado)
        layout.addView(boton)

        setContentView(layout)
    }

    private fun solicitarPermiso() {
        requestPermissionsLauncher.launch(permissions)
    }
}
