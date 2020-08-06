package com.erolaksoy.workstravelbook

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.custom_list_row.view.*

class CustomAdapter(val placesList : ArrayList<Place>, val context:Activity) :
    ArrayAdapter<Place>(context, R.layout.custom_list_row) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = context.layoutInflater
        val customView = layoutInflater.inflate(R.layout.custom_list_row,null,true)
        customView.listRowTextView.text = placesList.get(position).adress

        return customView
    }
}