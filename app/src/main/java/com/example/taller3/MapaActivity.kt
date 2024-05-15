package com.example.taller3

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MapaActivity : BarraActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var currentUserMarker: Marker? = null
    private var availableUserMarker: Marker? = null

    private lateinit var availableUserLocation: LatLng
    private lateinit var availableUserName: String
    private lateinit var availableUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        createLocationCallback()

        // Obtener los datos del Intent
        val availableUserLat = intent.getDoubleExtra("availableUserLat", 0.0)
        val availableUserLng = intent.getDoubleExtra("availableUserLng", 0.0)
        val availableUserName = intent.getStringExtra("availableUserName")
        val availableUserId = intent.getStringExtra("availableUserId")

        if (availableUserLat == 0.0 || availableUserLng == 0.0 || availableUserName == null || availableUserId == null) {
            Toast.makeText(this, "Error al obtener datos del usuario disponible.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        availableUserLocation = LatLng(availableUserLat, availableUserLng)
        this.availableUserName = availableUserName
        this.availableUserId = availableUserId
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    updateLocationOnMap(location)
                }
            }
        }
    }

    private fun updateLocationOnMap(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)

        // Actualizar Firebase con la nueva ubicación del usuario autenticado
        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            val userLocation = mapOf(
                "latitud" to location.latitude,
                "longitud" to location.longitude
            )
            database.child("users").child(uid).updateChildren(userLocation)
        }

        // Actualizar el pin verde en el mapa
        if (currentUserMarker == null) {
            currentUserMarker = map.addMarker(
                MarkerOptions()
                    .position(currentLatLng)
                    .title("Tu posición")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        } else {
            currentUserMarker?.position = currentLatLng
        }

        // Mover la cámara para enfocar en la nueva posición
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
            return
        }

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        startLocationUpdates()

        // Añadir marcador para la ubicación del usuario disponible
        availableUserMarker = map.addMarker(
            MarkerOptions()
                .position(availableUserLocation)
                .title("Usuario disponible: $availableUserName")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )

        // Escuchar cambios en la ubicación del usuario disponible
        database.child("users").child(availableUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latitud = snapshot.child("latitud").getValue(Double::class.java)
                val longitud = snapshot.child("longitud").getValue(Double::class.java)
                if (latitud != null && longitud != null) {
                    updateAvailableUserLocation(LatLng(latitud, longitud))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MapaActivity", "Error al escuchar cambios en la ubicación del usuario disponible: ", error.toException())
            }
        })
    }

    private fun updateAvailableUserLocation(newLocation: LatLng) {
        // Actualizar el pin azul en el mapa
        if (availableUserMarker != null) {
            availableUserMarker?.position = newLocation
        } else {
            availableUserMarker = map.addMarker(
                MarkerOptions()
                    .position(newLocation)
                    .title("Usuario disponible: $availableUserName")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }

        // Mover la cámara para enfocar en la nueva posición
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15f))
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
