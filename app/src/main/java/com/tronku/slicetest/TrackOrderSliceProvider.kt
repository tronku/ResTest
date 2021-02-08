package com.tronku.slicetest

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.telecom.Call
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder.ICON_IMAGE
import androidx.slice.builders.ListBuilder.INFINITY
import androidx.slice.builders.ListBuilder.SMALL_IMAGE
import androidx.slice.builders.SliceAction
import androidx.slice.builders.header
import androidx.slice.builders.list
import androidx.slice.builders.row
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class TrackOrderSliceProvider : SliceProvider() {

    private var orderStatus: String = "Checking..."
    private var deliveryTime: Int = 0
    private var deliveryGuy: String = "Checking..."

    private val source = "APP_ACTION"

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onBindSlice(sliceUri: Uri): Slice {
        // fetchOrderDetails()
        if (sliceUri.path != "/track") {
            return getErrorSlice(sliceUri)
        }
        if (deliveryTime == 0) {
            mockFetching(sliceUri)
        }
        return getMainSlice(sliceUri)
    }

    private fun getErrorSlice(sliceUri: Uri): Slice {
        return list(context!!, sliceUri, INFINITY) {
            row {
                title = "Something went wrong!"
            }
        }
    }

    private fun getMainSlice(sliceUri: Uri): Slice {
        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_ONE_SHOT)
        val openAppAction = SliceAction.create(
                pendingIntent,
                IconCompat.createWithResource(context, R.mipmap.ic_launcher),
                ICON_IMAGE,
                "Track"
        )
        val callAction = SliceAction.create(
                pendingIntent,
                IconCompat.createWithResource(context, R.drawable.circle_blue),
                SMALL_IMAGE,
                "Call"
        )
        return list(context!!, sliceUri, INFINITY) {
            header {
                title = "ResApp"
                subtitle = "Track your order"
                primaryAction = openAppAction
            }
            row {
                title = "Order status"
                subtitle = orderStatus
            }
            if (orderStatus != "DELIVERED") {
                row {
                    title = "Estimated time"
                    subtitle = "$deliveryTime min"
                }
                if (orderStatus == "ON THE WAY") {
                    row {
                        title = "Delivery Guy"
                        subtitle = deliveryGuy
                        addEndItem(callAction)
                    }
                }
            }
        }
    }

    private fun mockFetching(sliceUri: Uri) {
        runBlocking {
            delay(2000)
            orderStatus = "ON THE WAY"
            deliveryTime = 29
            deliveryGuy = "ResApp"
            context?.contentResolver?.notifyChange(sliceUri, null)
        }
    }
}
