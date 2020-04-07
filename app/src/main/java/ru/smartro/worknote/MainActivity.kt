package ru.smartro.worknote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.smartro.worknote.ui.login.LoginActivity
import ru.smartro.worknote.ui.login.LoginViewModel
import ru.smartro.worknote.ui.login.LoginViewModelFactory
import ru.smartro.worknote.ui.login.OrganisationSelectActivity
import ru.smartro.worknote.utils.commonViewModels.CurrentUserViewModel
import ru.smartro.worknote.utils.commonViewModels.CurrentUserViewModelFactory
import java.io.File

const val KEY_EVENT_ACTION = "key_event_action"
const val KEY_EVENT_EXTRA = "key_event_extra"
private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var currentUserViewModel: CurrentUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val currentUserViewModelFactory = CurrentUserViewModelFactory(this)
        currentUserViewModel = ViewModelProvider(this, currentUserViewModelFactory)
            .get(CurrentUserViewModel::class.java)
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(this)
        )
            .get(LoginViewModel::class.java)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_vehicle, R.id.nav_references, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setUpNavListeners(navView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                loginViewModel.logout()
                setResult(Activity.RESULT_OK)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setUpNavListeners(navView: NavigationView) {
        val headerView: View = navView.getHeaderView(0)
        val organisationNameText: TextView = headerView.findViewById(R.id.organisation_name_text)
        val userEmailText: TextView = headerView.findViewById(R.id.user_email_text)

        currentUserViewModel.currentUser.observe(this@MainActivity, Observer {
            if (it === null) {
                setResult(Activity.RESULT_OK)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return@Observer
            }
//            if (it.currentOrganisationId === null) {
//                setResult(Activity.RESULT_OK)
//                val intent = Intent(this, OrganisationSelectActivity::class.java)
//                startActivity(intent)
//                finish()
//                return@Observer
//            }
            userEmailText.text = it.email


        })
        currentUserViewModel.currentOrganisation.observe(this@MainActivity, Observer {

            organisationNameText.text = it?.name ?: ""
        })
    }


    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}
