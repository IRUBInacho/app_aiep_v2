package com.tutorial.app_aiep_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val confirmBtn = findViewById<Button>(R.id.confirmBtn)
        confirmBtn.setOnClickListener {
            selectedLatLng?.let {
                val intent = Intent()
                intent.putExtra("lat", it.latitude)
                intent.putExtra("lon", it.longitude)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Centrar el mapa en Chile por defecto
        val chileCenter = LatLng(-33.4372, -70.6506)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(chileCenter, 12f))

        map.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
        }
    }
}