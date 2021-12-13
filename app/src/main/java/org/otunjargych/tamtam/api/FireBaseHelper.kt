package org.otunjargych.tamtam.api

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FireBaseHelper {

    suspend fun DatabaseReference.valueEventFlow(): Flow<EventResponse> = callbackFlow {

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot): Unit =
                sendBlocking(EventResponse.Changed(snapshot))

            override fun onCancelled(error: DatabaseError): Unit =
                sendBlocking(EventResponse.Cancelled(error))
        }
        addListenerForSingleValueEvent(valueEventListener)
        awaitClose {
            removeEventListener(valueEventListener)
        }
    }
}