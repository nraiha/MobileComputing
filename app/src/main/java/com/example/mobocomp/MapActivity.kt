package com.example.mobocomp

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.toast
import java.util.*
import androidx.room.Room
import com.google.android.gms.location.*
import org.jetbrains.anko.doAsync
import android.graphics.Color



class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var gMap: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var selectedLocation: LatLng

    lateinit var geofencingClient: GeofencingClient
    val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
    val GEOFENCE_RADIUS = 500
    val GEOFENCE_EXPIRATION = 120*24*60*60*1000
    val GEOFENCE_DWELL_DELAY = 2*60*1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        (map_fragment as SupportMapFragment).getMapAsync(this)
        geofencingClient=LocationServices.getGeofencingClient(this)
        map_create.setOnClickListener {
            val reminderText = et_message.text.toString()
            if (reminderText.isEmpty()) {
                toast("Please provide reminder text")
                return@setOnClickListener
            }
            if (selectedLocation == null) {
                toast("please select location")
                return@setOnClickListener
            }
            val reminder = Reminder(
                uid = null,
                time = null,
                location = String.format(
                    "%.3f,%.3f",
                    selectedLocation.latitude,selectedLocation.latitude
                ),
                message = reminderText
            )

            doAsync{
                val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").build()
                val uid = db.reminderDao().insert(reminder).toInt()
                reminder.uid=uid

                db.close()
                CreateGeoFence(selectedLocation, reminder, geofencingClient)
            }
            finish()
        }
    }

    private fun CreateGeoFence(selectedLocation: LatLng, reminder: Reminder, geofencingClient: GeofencingClient) {
        val geofence = Geofence.Builder().setRequestId(GEOFENCE_ID).setCircularRegion(
                selectedLocation.latitude,
                selectedLocation.longitude,
                GEOFENCE_RADIUS.toFloat()).setExpirationDuration(
                    GEOFENCE_EXPIRATION.toLong()).setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
                            .setLoiteringDelay(GEOFENCE_DWELL_DELAY).build()
        val geofenceRequest = GeofencingRequest.Builder().setInitialTrigger(
                Geofence.GEOFENCE_TRANSITION_ENTER).addGeofence(geofence).build()

        val intent = Intent(
            this, GeoFenceReceiver::class.java).putExtra("uid", reminder.uid)
            .putExtra("message", reminder.message)
            .putExtra("location", reminder.location)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        geofencingClient.addGeofences(geofenceRequest,pendingIntent)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() && (grantResults[0]==PackageManager.PERMISSION_DENIED
                    || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
            toast("The reminder needs all the permissions to function")
        }
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
            if (grantResults.isNotEmpty() && (grantResults[2] == PackageManager.PERMISSION_DENIED)){
                toast("The reminder needs all the permissions to function")
            }
        }
    }
    override fun onMapReady(map: GoogleMap?) {
        gMap = map ?: return
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                                                == PackageManager.PERMISSION_GRANTED
            || (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                                            == PackageManager.PERMISSION_GRANTED)) {
            gMap.isMyLocationEnabled = true
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                if (location != null) {
                    var latLong = LatLng(location.latitude, location.longitude)
                    with(gMap) {
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f))
                    }
                }

            }
        } else {
            var permission = mutableListOf<String>()
            permission.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            permission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                permission.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                123
            )
        }
        gMap.setOnMapClickListener { location: LatLng ->
            with(gMap) {
                clear()
                animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))

                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                var title = ""
                var city = ""
                try {
                    val addressList = geocoder.getFromLocation(
                        location.latitude, location.longitude, 1)
                    city = addressList.get(0).locality
                    title = addressList.get(0).getAddressLine(0)
                } catch (e:Exception){ }

                val marker =
                    addMarker(MarkerOptions().position(location).snippet(title).title(city))
                marker.showInfoWindow()

                addCircle(com.google.android.gms.maps.model.CircleOptions().center(location)
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(100, 150, 150, 150)))
                selectedLocation = location
            }
        }
    }
}