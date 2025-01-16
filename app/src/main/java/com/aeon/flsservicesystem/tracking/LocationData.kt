package com.aeon.flsservicesystem.tracking

class LocationData (private var latitude: String,
                    private var longitude: String,
                    private var battery: String,
                    private var speed: String){

    var trackingLatitude: String
        get() = latitude
        set(value) {latitude = value}
    var trackingLongitude: String
        get() = longitude
        set(value) {longitude = value}
    var trackingBattery: String
        get() = battery
        set(value) {battery = value}
    var trackingSpeed: String
        get() = speed
        set(value) {speed = value}

    fun displayInfo()
    {
        println("Latitude : $trackingLatitude, Longitude : $trackingLongitude, Battery : $trackingBattery, Speed : $trackingSpeed")
    }

}