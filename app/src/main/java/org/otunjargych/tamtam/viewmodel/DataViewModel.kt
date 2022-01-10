package org.otunjargych.tamtam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.api.EventResponse
import org.otunjargych.tamtam.api.FireBaseHelper.lastValuesEventFlow
import org.otunjargych.tamtam.api.FireBaseHelper.valuesEventFlow
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.State


class DataViewModel : ViewModel() {

    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppChildEventListener
    private val list: MutableList<Note> = ArrayList()
    private val _work: MutableLiveData<State<List<Note>>> = MutableLiveData()
    val work: LiveData<State<List<Note>>> = _work

    private val _note: MutableLiveData<List<Note>> = MutableLiveData()
    val note: LiveData<List<Note>> = _note

    private val _other: MutableLiveData<Note> = MutableLiveData()
    val other: LiveData<Note> = _other

    private val _transport: MutableLiveData<State<List<Note>>> = MutableLiveData()
    val transport: LiveData<State<List<Note>>> = _transport

    private val _last: MutableLiveData<State<List<Note>>> = MutableLiveData()
    val last: LiveData<State<List<Note>>> = _last

    private val _medicine: MutableLiveData<State<List<Note>>> = MutableLiveData()
    val medicine: LiveData<State<List<Note>>> = _medicine


    fun loadWorkData() {
        val list: MutableList<Note> = ArrayList()
        _work.postValue(State.Loading())
        viewModelScope.launch {
            delay(1500)
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
            mRefAds.valuesEventFlow().collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!
                            list.add(0, note)
                            _work.postValue(State.Success(list))
                        }

                    }
                    is EventResponse.Cancelled -> {
                        _work.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }

    fun loadTransportData() {
        val list: MutableList<Note> = ArrayList()
        _transport.postValue(State.Loading())
        viewModelScope.launch {
            delay(1000)
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_TRANSPORT)
            mRefAds.valuesEventFlow().collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!
                            if (!list.contains(note)) {
                                list.add(0, note)
                                _transport.postValue(State.Success(list))
                            }

                        }

                    }
                    is EventResponse.Cancelled -> {
                        _transport.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }

    fun loadMedicineAndBeautyData() {
        val list: MutableList<Note> = ArrayList()
        _medicine.postValue(State.Loading())
        viewModelScope.launch {
            delay(1000)
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_HEALTH)
            mRefAds.valuesEventFlow().collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!
                            if (!list.contains(note)) {
                                list.add(0, note)
                                _medicine.postValue(State.Success(list))
                            }

                        }

                    }
                    is EventResponse.Cancelled -> {
                        _medicine.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }

    fun loadLastNoteData(count: Int) {
        val list: MutableList<Note> = ArrayList()
        _last.postValue(State.Loading())
        viewModelScope.launch {
            delay(1500)
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
            mRefAds.lastValuesEventFlow(count).collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!
                            if (!list.contains(note)) {

                            }
                            list.add(0, note)
                            _last.postValue(State.Success(list))

                        }

                    }
                    is EventResponse.Cancelled -> {
                        _last.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }

    fun getData() {
        viewModelScope.launch {
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
            mRefAds.lastValuesEventFlow(5).collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!

                            list.add(0, note)
                            _note.value = list


                        }

                    }
                    is EventResponse.Cancelled -> {
                        _last.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }


    fun getOther(count: Int) {
        mRefAds = REF_DATABASE_ROOT.child(NODE_WORKS)
        mRefListener = AppChildEventListener {
            var note = it.getValue(Note::class.java)!!
            _other.value = note


        }
        mRefAds.limitToLast(count).addChildEventListener(mRefListener)
    }


}