package com.thesis.distanceguard.presentation.detail

import android.graphics.Color

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.R
import com.thesis.distanceguard.databinding.FragmentDetailBinding
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.room.entities.CountryEntity
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber

class DetailFragment(private val itemCountry: CountryEntity) : BaseFragment<FragmentDetailBinding>() {

    companion object {
        const val TAG = "DetailFragment"
        private const val animationDuration = 1000L
    }

    private lateinit var detailViewModel: DetailViewModel

    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)
        setupViewModel()
        setupLineChart()
        Timber.d("${itemCountry.countryInfoEntity}")
        fetchCountry()
        updateOtherInformation()
    }

    private fun setupViewModel() {
        AndroidSupportInjection.inject(this)
        detailViewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
        detailViewModel.errorMessage.observe(this, Observer {
            hideDialog()
            showToastMessage(it)
        })
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        setupToolbarTitle(itemCountry.country!!)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        setupToolbarTitle("Countries")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupLineChart() {
        binding.apply {
            chartCases.gradientFillColors =
                intArrayOf(
                    Color.parseColor("#99caff"),
                    Color.TRANSPARENT
                )
            chartCases.animation.duration = animationDuration


            chartRecovered.gradientFillColors =
                intArrayOf(
                    Color.parseColor("#a6ecbb"),
                    Color.TRANSPARENT
                )
            chartRecovered.animation.duration = animationDuration


            chartDeath.gradientFillColors =
                intArrayOf(
                    Color.parseColor("#ffb9b9"),
                    Color.TRANSPARENT
                )
            chartDeath.animation.duration = animationDuration
        }
    }

    private fun fetchCountry() {
        showProgressDialog("Fetching data")
        detailViewModel.fetchCountry(itemCountry.country!!)
            .observe(this, countryObserver)
    }

    private val countryObserver = Observer<HistoricalCountryEntity?> {
        if(it == null){
            binding.apply {
                chartCases.visibility = View.INVISIBLE
                chartRecovered.visibility = View.INVISIBLE
                chartDeath.visibility = View.INVISIBLE
            }
        }
        it?.let {
            hideDialog()
            binding.apply {
                chartCases.visibility = View.VISIBLE
                chartRecovered.visibility = View.VISIBLE
                chartDeath.visibility = View.VISIBLE
                chartCases.animate(
                    AppUtil.convertPairLongToPairFloat(
                        it.timeline!!.cases!!.toList().sortedBy { value -> value.second })
                )

                chartRecovered.animate(
                    AppUtil.convertPairLongToPairFloat(
                        it.timeline!!.recovered!!.toList().sortedBy { value -> value.second })
                )

                chartDeath.animate(
                    AppUtil.convertPairLongToPairFloat(
                        it.timeline!!.deaths!!.toList().sortedBy { value -> value.second })
                )

                //update text view
                val length = it.timeline!!.cases!!.toList().size - 1
                tvComfirmedCount.text = AppUtil.toNumberWithCommas(
                    it.timeline!!.cases!!.toList().sortedBy { value -> value.second }[length].second
                )
                tvRecoveredCount.text = AppUtil.toNumberWithCommas(
                    it.timeline!!.recovered!!.toList().sortedBy { value -> value.second }[length].second
                )
                tvDeathCount.text = AppUtil.toNumberWithCommas(
                    it.timeline!!.deaths!!.toList().sortedBy { value -> value.second }[length].second
                )
            }
        }

    }


    private fun updateOtherInformation() {
        binding.apply {
            tvTestCount.text = AppUtil.toNumberWithCommas(itemCountry.tests!!.toLong())
            tvPopulationCount.text = AppUtil.toNumberWithCommas(itemCountry.population!!.toLong())
            tvOneCasePerPeopleCount.text =
                AppUtil.toNumberWithCommas(itemCountry.oneCasePerPeople!!.toLong())
            tvOneTestPerPeopleCount.text =
                AppUtil.toNumberWithCommas(itemCountry.oneTestPerPeople!!.toLong())
            tvOneDeathPerPeopleCount.text =
                AppUtil.toNumberWithCommas(itemCountry.oneDeathPerPeople!!.toLong())
            tvCasePerOneMillionCount.text =
                AppUtil.toNumberWithCommas(itemCountry.casesPerOneMillion!!.toLong())
            tvDeathPerOneMillionCount.text =
                AppUtil.toNumberWithCommas(itemCountry.deathsPerOneMillion!!.toLong())
            tvTestPerOneMillionCount.text =
                AppUtil.toNumberWithCommas(itemCountry.testsPerOneMillion!!.toLong())
            tvActivePerOneMillionCount.text =
                AppUtil.toNumberWithCommas(itemCountry.activePerOneMillion!!.toLong())
        }

    }
}