package jt.projects.gbmaps.ui

import android.Manifest
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import jt.projects.gbmaps.R
import jt.projects.gbmaps.databinding.ActivityMainBinding
import jt.projects.gbmaps.utils.showToast

class MainActivity : PermissionActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_markers, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        checkPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            "Запрос местоположения",
            "Требуется для отображения карты",
            onSuccess = {},
            onFailed = {
                showToast("Без получения разрешений дальнейшая работа невозможна")
                finish()
            }
        )
    }


}