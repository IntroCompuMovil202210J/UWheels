package com.abmodel.uwheels.ui.passenger.ride.request

import android.util.Log
import androidx.lifecycle.*
import com.abmodel.uwheels.BuildConfig.MAPS_API_KEY
import com.abmodel.uwheels.network.maps.DirectionsApi
import com.abmodel.uwheels.network.maps.DirectionsResponse
import com.abmodel.uwheels.network.maps.DirectionsRoute
import com.abmodel.uwheels.data.model.CustomAddress
import com.abmodel.uwheels.util.PolyUtil
import com.abmodel.uwheels.util.parseLatLng
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestRideViewModel: ViewModel() {

	private val _sourceAddress = MutableLiveData<CustomAddress?>(null)
	val sourceAddress: MutableLiveData<CustomAddress?>
		get() = _sourceAddress

	private val _destinationAddress = MutableLiveData<CustomAddress?>(null)
	val destinationAddress: MutableLiveData<CustomAddress?>
		get() = _destinationAddress

	private val _route = MutableLiveData<List<LatLng>?>(null)
	val route: MutableLiveData<List<LatLng>?>
		get() = _route

	fun updateSourceAddress(address: CustomAddress?) {
		if (address != null) {
			_sourceAddress.value = address
			calculateRoute()
		}
	}

	fun updateDestinationAddress(address: CustomAddress?) {
		if (address != null) {
			_destinationAddress.value = address
			calculateRoute()
		}
	}

	private fun calculateRoute() {
		if (_sourceAddress.value != null &&
			_destinationAddress.value != null
		) {
			viewModelScope.launch(Dispatchers.IO) {
				try {
					val response: DirectionsResponse =
						DirectionsApi.retrofitService.getDirections(
							parseLatLng(sourceAddress.value!!.latLng!!),
							parseLatLng(destinationAddress.value!!.latLng!!),
							"co",
							MAPS_API_KEY
						)

					if (response.routes?.isNotEmpty() == true) {
						val route: DirectionsRoute = response.routes[0]

						val points: MutableList<LatLng> = mutableListOf()
						route.legs.forEach { leg ->
							leg.steps.forEach { step ->
								points.addAll(
									PolyUtil.decode(step.polyline.points)
								)
							}
						}

						_route.postValue(points)
					}
				} catch (e: Exception) {
					Log.e(RequestRideFragment.TAG, "Error calculating route", e)
				}
			}
		} else {
			_route.value = null
		}
	}
}

/*
class RequestRideViewModelFactory(
	private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(RequestRideViewModel::class.java)) {
			@Suppress("unchecked_cast")
			return RequestRideViewModel(application) as T
		} else {
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
}
*/