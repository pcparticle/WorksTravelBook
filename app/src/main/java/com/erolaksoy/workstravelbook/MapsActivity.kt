package com.erolaksoy.workstravelbook

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import android.location.LocationListener as LocationListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(myListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location?) {
                if(location!=null) {
                    val newUserLocation = LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation,15f))
                }
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("Not yet implemented")
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("Not yet implemented")
            }

        }

        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener)
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                val lastLocationLng = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLng, 6f))
            }

        }
        // Add a marker in Sydney and move the camera

    }

    private val myListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {

            val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())
            var adress = ""
            if(p0!=null){
                val adressList = geoCoder.getFromLocation(p0.latitude,p0.longitude,1)
                if(adressList.size > 0 && adressList!=null){
                    adress = adressList[0].thoroughfare + adressList[0].subThoroughfare

                }
            }else{
                adress = "New Place"
            }

            mMap.addMarker(MarkerOptions().position(p0!!).title(adress))
            val newPlace = Place(adress,p0.latitude,p0.longitude)
            val dialog = AlertDialog.Builder(this@MapsActivity)
            dialog.setCancelable(false)
            dialog.setTitle("Are you sure ?")
            dialog.setMessage(adress)
            dialog.setPositiveButton("Yes"){dialog, which ->

                //TODO: SQLITE SAVE
                try{
                    val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS places (adress VARCHAR, latitude DOUBLE , longitude DOUBLE)")
                    val toCompile = "INSERT INTO places (adress,latitude,longitude) VALUES(? , ? ,? )"
                    val sqlLiteStatement = database.compileStatement(toCompile)
                    sqlLiteStatement.bindString(1,newPlace.adress)
                    sqlLiteStatement.bindDouble(2,newPlace.latitude!!)
                    sqlLiteStatement.bindDouble(3,newPlace.longitude!!)
                    sqlLiteStatement.execute()

                    Toast.makeText(this@MapsActivity,"Adress is saved $adress",Toast.LENGTH_LONG).show()
                }catch (error:Error){error.printStackTrace()}

            }.setNegativeButton("No"){dialog, which->
                Toast.makeText(this@MapsActivity,"Cancelled",Toast.LENGTH_LONG).show()
            }
            dialog.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if(grantResults.isNotEmpty()){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}