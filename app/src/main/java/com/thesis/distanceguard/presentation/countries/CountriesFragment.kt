package com.thesis.distanceguard.presentation.countries

import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.databinding.FragmentCountriesBinding
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.room.entities.CountryEntity
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class CountriesFragment : BaseFragment<FragmentCountriesBinding>() {

    private lateinit var countriesViewModel: CountriesViewModel
    private lateinit var countriesAdapter: CountriesAdapter
    override fun getResLayoutId(): Int {
        return R.layout.fragment_countries
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setHasOptionsMenu(true)
        setupViewModel()
        setupRecyclerView()

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filteredList = CountriesAdapter.filter(
                    countriesViewModel.countryList.value!!,
                    p0.toString()
                )
                countriesAdapter.replaceAll(filteredList!!)
                binding.rvCountries.scrollToPosition(0)
            }
        })
        binding.imgClear.setOnClickListener {
            binding.edtSearch.text.clear()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_countries,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_reload -> {
                showProgressDialog("Fetching Data")
                countriesViewModel.fetchCountryList()
            }
        }
        return true
    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        countriesViewModel =
            ViewModelProvider(this, viewModelFactory).get(CountriesViewModel::class.java)

        countriesViewModel.fetchCountryList().observe(this, countryListObserver)

        countriesViewModel.errorMessage.observe(this, Observer {
            hideDialog()
            showToastMessage(it)
        })
    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(context)
        countriesAdapter = CountriesAdapter(context!!)
        binding.rvCountries.apply {
            layoutManager = linearLayoutManager
            adapter = countriesAdapter
        }

        countriesAdapter.itemClickListener = object :
            CountriesAdapter.ItemClickListener<CountryEntity> {
            override fun onClick(position: Int, item: CountryEntity) {
                Timber.d("onClick: $item")

                ViewCompat.postOnAnimationDelayed(view!!, // Delay to show ripple effect
                    Runnable {
                        val mainActivity = activity as MainActivity
                        mainActivity.addFragment(
                            DetailFragment(item),
                            DetailFragment.TAG,
                            R.id.container_main
                        )
                    }
                    , 50)

            }

            override fun onLongClick(position: Int, item: CountryEntity) {}
        }
    }

    private val countryListObserver = Observer<ArrayList<CountryEntity>> {
        hideDialog()
        it?.let {
            if(it.isNotEmpty()){
                binding.rlEmpty.visibility = View.GONE
                countriesAdapter.add(it)
            } else {
                binding.rlEmpty.visibility = View.VISIBLE
            }

        }
    }
}