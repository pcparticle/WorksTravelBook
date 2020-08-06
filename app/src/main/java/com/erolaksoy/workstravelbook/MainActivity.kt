package com.erolaksoy.workstravelbook

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_place_option) {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SQLITE OPERATIONS
        try{
            val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE,null)
            val myCursor =database.rawQuery("Select * From places",null)
            val adressIndex =myCursor.getColumnIndex("adress")
            val latitudeIndex =myCursor.getColumnIndex("latitude")
            val longitudeIndex =myCursor.getColumnIndex("longitude")

            while(myCursor.moveToNext()) {
                val adressFromDatabase = myCursor.getString(adressIndex)
                val latitudeFromDatabase = myCursor.getDouble(latitudeIndex)
                val longitudeFromDatabase = myCursor.getDouble(longitudeIndex)
                val myCurrentSavedPlace = Place(adressFromDatabase,latitudeFromDatabase,longitudeFromDatabase)
                println(myCurrentSavedPlace.adress + myCurrentSavedPlace.latitude)
            }
            myCursor.close()


        }catch(e : Exception){e.printStackTrace()}

    }

}