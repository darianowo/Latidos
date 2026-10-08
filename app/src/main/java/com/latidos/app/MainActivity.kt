package com.latidos.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class MainActivity : ComponentActivity() {

    private lateinit var estado: TextView
    private lateinit var bpmTexto: TextView

    private val handler = Handler(Looper.getMainLooper())

    private val actualizarAutomaticamente = object : Runnable {
        override fun run() {
            leerLatidos()

            handler.postDelayed(this, 120_000)
        }
    }

    private val permisos = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class)
    )

    private val solicitarPermisos =
        registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { permisosConcedidos ->

            if (permisosConcedidos.containsAll(permisos)) {
                estado.text = "✅ Permiso concedido"
                leerLatidos()
            } else {
                estado.text = "❌ Permiso no concedido"
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

        bpmTexto = TextView(this)
        bpmTexto.text = "❤️ -- BPM"
        bpmTexto.textSize = 42f
        bpmTexto.setPadding(0, 60, 0, 40)

        estado = TextView(this)
        estado.text = "Comprobando Health Connect..."
        estado.textSize = 18f

        val botonPermiso = Button(this)
        botonPermiso.text = "Dar permiso a Health Connect"

        botonPermiso.setOnClickListener {
            solicitarPermisos.launch(permisos)
        }

        val botonActualizar = Button(this)
        botonActualizar.text = "Actualizar ❤️"

        botonActualizar.setOnClickListener {
            leerLatidos()
        }

        layout.addView(titulo)
        layout.addView(bpmTexto)
        layout.addView(estado)
        layout.addView(botonPermiso)
        layout.addView(botonActualizar)

        setContentView(layout)

        comprobarHealthConnect()
    }

    override fun onResume() {
        super.onResume()

        handler.post(actualizarAutomaticamente)
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(actualizarAutomaticamente)
    }

    private fun comprobarHealthConnect() {

        when (HealthConnectClient.getSdkStatus(this)) {

            HealthConnectClient.SDK_AVAILABLE -> {
                estado.text = "🟢 Health Connect disponible"
                comprobarPermiso()
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                estado.text = "🟡 Health Connect necesita actualizarse"
            }

            HealthConnectClient.SDK_UNAVAILABLE -> {
                estado.text = "🔴 Health Connect no está disponible"
            }

            else -> {
                estado.text = "⚠️ Estado desconocido"
            }
        }
    }

    private fun comprobarPermiso() {

        lifecycleScope.launch {

            val healthConnectClient =
                HealthConnectClient.getOrCreate(this@MainActivity)

            val permisosConcedidos =
                healthConnectClient.permissionController
                    .getGrantedPermissions()

            if (permisosConcedidos.containsAll(permisos)) {

                estado.text =
                    "🟢 Permiso concedido. Leyendo latidos..."

                leerLatidos()

            } else {

                estado.text =
                    "🟡 Falta dar permiso"
            }
        }
    }

    private fun leerLatidos() {

        lifecycleScope.launch {

            try {

                val healthConnectClient =
                    HealthConnectClient.getOrCreate(this@MainActivity)

                val ahora = Instant.now()

                val hace24Horas =
                    ahora.minus(Duration.ofHours(24))

                val respuesta =
                    healthConnectClient.readRecords(
                        ReadRecordsRequest(
                            recordType = HeartRateRecord::class,
                            timeRangeFilter =
                                TimeRangeFilter.between(
                                    hace24Horas,
                                    ahora
                                )
                        )
                    )

                val muestras =
                    respuesta.records.flatMap { record ->
                        record.samples
                    }

                if (muestras.isEmpty()) {

                    bpmTexto.text = "❤️ -- BPM"

                    estado.text =
                        "⚠️ No encontramos latidos"

                    return@launch
                }

                val ultimaMuestra =
                    muestras.maxByOrNull { it.time }

                if (ultimaMuestra != null) {

                    bpmTexto.text =
                        "❤️ ${ultimaMuestra.beatsPerMinute} BPM"

                    estado.text =
                        "🟢 Actualizado automáticamente"

                }

            } catch (e: Exception) {

                estado.text =
                    "❌ Error: ${e.message}"
            }
        }
    }
}
