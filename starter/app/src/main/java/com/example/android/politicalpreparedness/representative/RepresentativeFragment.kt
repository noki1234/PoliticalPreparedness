package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment(), AdapterView.OnItemSelectedListener{

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isSpinnerOpened = false

    private lateinit var binding : FragmentRepresentativeBinding
    companion object {
        //TODO: Add Constant for Location request
        const val REQUEST_LOCATION_PERMISSION = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29

    }

    //TODO: Declare ViewModel
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val viewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //TODO: Define and assign Representative adapter
        val representativeListAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = representativeListAdapter

        //TODO: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                representativeListAdapter.submitList(it)
            }
        })

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            var address = Address(binding.addressLine1.text.toString(),
                                 binding.addressLine2.text.toString(),
                                binding.city.text.toString(),
                                binding.stateSpinner.selectedItem.toString(),
                                binding.zip.text.toString()
            )
            viewModel.setAddress(address)
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
            checkDeviceLocationSettingsAndAskForTurnOnGps()
            hideKeyboard()
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == ApiStatus.ERROR){
                Toast.makeText(requireContext(), R.string.search_not_found, Toast.LENGTH_SHORT)
            }
        })

        viewModel.address.observe(viewLifecycleOwner, Observer {
            viewModel.getRepresentatives()
        })

        binding.stateSpinner.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> isSpinnerOpened = true
                }
                return v?.onTouchEvent(event) ?: true
            }
        })


        //Set Spinner
        setSpinner()

        return binding.root

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //TODO: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                checkDeviceLocationSettingsAndAskForTurnOnGps()
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.location_access), Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
     }

    private fun getLocation() {
        if (checkLocationPermissions()) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                binding.viewModel!!.setAddress(geoCodeLocation(it))
            }
            fusedLocationClient.lastLocation.addOnFailureListener {
                Toast.makeText(requireContext(), "Not able to get your actual location!", Toast.LENGTH_SHORT).show()
            }
            //TODO: Get location from LocationServices
            //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        }
    }

    fun checkDeviceLocationSettingsAndAskForTurnOnGps(resolve: Boolean = true) {

        //Step 1: create a LocationRequest, a LocationSettingsRequest Builder.
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        //Step 2: Use LocationServices to get the Settings Client and create a val called locationSettingsResponseTask to check the location settings
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())

        //Step 3: add an onFailureListener() to the locationSettingsResponseTask
        locationSettingsResponseTask.addOnFailureListener { exception ->
            // if the exception is of type ResolvableApiException -> call startResolutionForResult() - to prompt the user to turn on device location.
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                            exception.resolution.intentSender,
                            REQUEST_TURN_DEVICE_LOCATION_ON,
                            null,
                            0,
                            0,
                            0,
                            null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("ReminderListFragment: ", "Error getting location settings resolution: " + sendEx.message)
                }
                //exception is not of type ResolvableApiException -> present a snackbar that alerts the user that location needs to be enabled to play the treasure hunt.
            } else {
                Snackbar.make(
                        binding.motionLayout,
                        R.string.location_disbaled, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndAskForTurnOnGps()
                }.show()
            }
        }

        locationSettingsResponseTask.addOnSuccessListener {
            getLocation()
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    //SpinnerPart

    private fun setSpinner(){
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.states,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.stateSpinner.adapter = adapter
            binding.stateSpinner.onItemSelectedListener = this
        }
    }



    private fun clearFields(){
        binding.addressLine1.text.clear()
        binding.addressLine2.text.clear()
        binding.city.text.clear()
        binding.zip.text.clear()
    }


    //Spinner selection listeners
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (isSpinnerOpened) {
            clearFields()
        }
        isSpinnerOpened = false
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}



}