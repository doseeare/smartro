package ru.smartro.worknote.ui.platform_service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_platform_service.*
import kotlinx.android.synthetic.main.alert_accept_task.view.*
import kotlinx.android.synthetic.main.alert_accept_task.view.accept_btn
import kotlinx.android.synthetic.main.alert_fill_kgo.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.container_service.ContainerAdapter
import ru.smartro.worknote.extensions.fillKgoVolume
import ru.smartro.worknote.extensions.hideDialog
import ru.smartro.worknote.extensions.toast
import ru.smartro.worknote.extensions.warningCameraShow
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.database.entity.work_order.ContainerEntity
import ru.smartro.worknote.service.database.entity.work_order.PlatformEntity
import ru.smartro.worknote.ui.camera.CameraActivity
import ru.smartro.worknote.ui.problem.ExtremeProblemActivity
import ru.smartro.worknote.util.PhotoTypeEnum
import ru.smartro.worknote.util.StatusEnum


class PlatformServiceActivity : AppCompatActivity(), ContainerAdapter.ContainerPointClickListener {
    private val REQUEST_EXIT = 33
    private lateinit var platformEntity: PlatformEntity
    private val viewModel: PlatformServiceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_platform_service)
        intent.let {
            platformEntity = viewModel.findPlatformEntity(it.getIntExtra("platform_id", 0))
        }
        supportActionBar?.title = "${platformEntity.address}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initContainer()
        initBeforeMedia()

        problem_btn.setOnClickListener {
            val intent = Intent(this, ExtremeProblemActivity::class.java)
            intent.putExtra("platform_id", platformEntity.platformId)
            intent.putExtra("isContainerProblem", false)
            startActivityForResult(intent, REQUEST_EXIT)
        }
        kgo_btn.setOnClickListener {
            val intent = Intent(this@PlatformServiceActivity, CameraActivity::class.java)
            intent.putExtra("platform_id", platformEntity.platformId)
            intent.putExtra("photoFor", PhotoTypeEnum.forKGO)
            startActivityForResult(intent, 101)
            hideDialog()
        }
    }

    private fun initBeforeMedia() {
        warningCameraShow("???????????????? ???????? ???? ???? ????????????????????????").run {
            AppPreferences.serviceStartedAt = System.currentTimeMillis() / 1000L
            this.accept_btn.setOnClickListener {
                val intent = Intent(this@PlatformServiceActivity, CameraActivity::class.java)
                intent.putExtra("platform_id", platformEntity.platformId)
                intent.putExtra("photoFor", PhotoTypeEnum.forBeforeMedia)
                startActivity(intent)
                hideDialog()
            }
            this.dismiss_btn.setOnClickListener {
                hideDialog()
                finish()
            }
        }
    }

    private fun initAfterMedia() {
        if (viewModel.findPlatformEntity(platformEntity.platformId!!).containers.filter {
                it.isActiveToday == true }.all{ it.status != StatusEnum.NEW }){
            complete_task_btn.isVisible = true
            complete_task_btn.setOnClickListener {
                warningCameraShow("???????????????? ???????? ???? ?????????? ????????????????????????").let {
                    it.accept_btn.setOnClickListener {
                        val intent = Intent(this@PlatformServiceActivity, CameraActivity::class.java)
                        intent.putExtra("platform_id", platformEntity.platformId!!)
                        intent.putExtra("photoFor", PhotoTypeEnum.forAfterMedia)
                        startActivityForResult(intent, 13)
                        hideDialog()
                    }
                    it.dismiss_btn.setOnClickListener {
                        hideDialog()
                    }
                }
            }
        } else {
            complete_task_btn.isVisible = false
        }
    }

    private fun initContainer() {
        val platform = viewModel.findPlatformEntity(platformId = platformEntity.platformId!!)
        platform_service_rv.adapter = ContainerAdapter(this, platform.containers)
        point_info_tv.text = "???${platform.srpId} / ${platform.containers!!.size} ????????."
    }

    override fun startContainerService(item: ContainerEntity) {
        val intent = Intent(this, ContainerServiceActivity::class.java)
        intent.putExtra("container_id", item.containerId)
        intent.putExtra("platform_id", platformEntity.platformId)
        startActivityForResult(intent, 14)
    }

    override fun onBackPressed() {
        toast("?????????????????? ????????????")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == Activity.RESULT_OK) {
            viewModel.updatePlatformStatus(platformEntity.platformId!!, StatusEnum.SUCCESS)
            setResult(Activity.RESULT_OK)
            finish()
        }
        else if (requestCode == 14) {
                if (resultCode == Activity.RESULT_OK) {
                    initContainer()
                    initAfterMedia()
                }
            } else if (requestCode == REQUEST_EXIT) {
            if (resultCode == 99) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        } else if (resultCode == 101 && requestCode == 101) {
            fillKgoVolume().let { view->
                view.accept_btn.setOnClickListener {
                    if (!view.kgo_volume_in.text.isNullOrEmpty()) {
                        val kgoVolume = view.kgo_volume_in.text.toString().toDouble()
                        viewModel.updatePlatformKGO(platformEntity.platformId!!, kgoVolume)
                        hideDialog()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initContainer()
        initAfterMedia()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
