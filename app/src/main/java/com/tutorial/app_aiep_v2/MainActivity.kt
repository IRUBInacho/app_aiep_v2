package com.tutorial.app_aiep_v2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationTv: TextView
    private lateinit var btnUbicacion: Button
    private lateinit var logoutBtn: Button
    private lateinit var compraInput: EditText
    private lateinit var costoDespachoTv: TextView
    private lateinit var btnSeleccionarUbicacion: Button

    private var referenciaLat = -33.4372
    private var referenciaLon = -70.6506

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationTv = findViewById(R.id.locationTv)
        btnUbicacion = findViewById(R.id.btnUbicacion)
        logoutBtn = findViewById(R.id.logoutBtn)
        compraInput = findViewById(R.id.compraInput)
        costoDespachoTv = findViewById(R.id.costoDespachoTv)
        btnSeleccionarUbicacion = findViewById(R.id.btnSeleccionarUbicacion)

        btnUbicacion.setOnClickListener {
            getCurrentLocation()
        }

        btnSeleccionarUbicacion.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            startActivityForResult(mapIntent, 100)
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            referenciaLat = data?.getDoubleExtra("lat", referenciaLat) ?: referenciaLat
            referenciaLon = data?.getDoubleExtra("lon", referenciaLon) ?: referenciaLon
        }
    }

    private fun getCurrentLocation() {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        val coarse = Manifest.permission.ACCESS_COARSE_LOCATION
        val hasFine = ActivityCompat.checkSelfPermission(this, fine) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(this, coarse) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            ActivityCompat.requestPermissions(this, arrayOf(fine), 1)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                mostrarResultado(location)
            } else {
                val cts = CancellationTokenSource()
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cts.token
                ).addOnSuccessListener { loc2: Location? ->
                    if (loc2 != null) {
                        mostrarResultado(loc2)
                    } else {
                        locationTv.text = getString(R.string.error_ubicacion)
                        locationTv.visibility = TextView.VISIBLE
                    }
                }
            }
        }
    }

    private fun mostrarResultado(location: Location) {
        val latitud = location.latitude
        val longitud = location.longitude
        val distanciaKm = haversineKm(latitud, longitud, referenciaLat, referenciaLon)

        locationTv.text = getString(
            R.string.ubicacion,
            latitud.toString(),
            longitud.toString(),
            distanciaKm
        )
        locationTv.visibility = TextView.VISIBLE

        val montoCompra = compraInput.text.toString().toDoubleOrNull() ?: 0.0
        val costoDespacho = calcularDespacho(montoCompra, distanciaKm)
        costoDespachoTv.text = "Costo de despacho: \$${costoDespacho}"

        val despachoData = hashMapOf(
            "latitud" to latitud,
            "longitud" to longitud,
            "distanciaKm" to distanciaKm,
            "montoCompra" to montoCompra,
            "costoDespacho" to costoDespacho,
            "referenciaLat" to referenciaLat,
            "referenciaLon" to referenciaLon
        )

        db.collection("despachos")
            .add(despachoData)
            .addOnSuccessListener { ref ->
                Log.d("FIREBASE", "Despacho guardado con ID: ${ref.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error al guardar despacho", e)
            }
    }

    private fun calcularDespacho(monto: Double, distanciaKm: Double): Int {
        return when {
            monto >= 50000 && distanciaKm <= 20 -> 0
            monto in 25000.0..49999.99 -> (distanciaKm * 150).roundToInt()
            else -> (distanciaKm * 300).roundToInt()
        }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            locationTv.text = getString(R.string.permiso_denegado)
            locationTv.visibility = TextView.VISIBLE
        }
    }
}