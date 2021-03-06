package ru.smartro.worknote.ui.choose.vehicle_2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_choose.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.VehicleAdapter
import ru.smartro.worknote.extensions.loadingHide
import ru.smartro.worknote.extensions.loadingShow
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.Status
import ru.smartro.worknote.service.network.response.vehicle.Vehicle
import ru.smartro.worknote.ui.choose.way_list_3.WayListActivity
import ru.smartro.worknote.util.MyUtil

class VehicleActivity : AppCompatActivity() {
    private val viewModel: VehicleViewModel by viewModel()
    private lateinit var adapter: VehicleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        supportActionBar?.title = "Выберите автомобиль"

        loadingShow()
        viewModel.getVehicle(AppPreferences.organisationId).observe(this, Observer { result ->
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    adapter = VehicleAdapter(data?.data as ArrayList<Vehicle>)
                    choose_rv.adapter = adapter
                    loadingHide()
                }
                Status.ERROR -> {
                    toast(result.msg)
                    loadingHide()
                }
                Status.NETWORK -> {
                    toast("Проблемы с интернетом")
                    loadingHide()
                }
            }

        })

        next_btn.setOnClickListener {
            if (adapter.getSelectedId() != -1) {
                AppPreferences.vehicleId = adapter.getSelectedId()
                startActivity(Intent(this, WayListActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout_organisation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtil.onMenuOptionClicked(this, item.itemId)
        return super.onOptionsItemSelected(item)
    }
}