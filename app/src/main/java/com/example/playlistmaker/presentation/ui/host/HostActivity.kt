package com.example.playlistmaker.presentation.ui.host

import android.os.Bundle
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import pub.devrel.easypermissions.EasyPermissions
import com.example.playlistmaker.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityHostBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        requestPermissions()
    }

    private fun requestPermissions() {
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val permissionsNeeded = mutableListOf<String>()

        if (!EasyPermissions.hasPermissions(this, storagePermission)) {
            permissionsNeeded.add(storagePermission)
        }

        if (permissionsNeeded.isNotEmpty()) {
            EasyPermissions.requestPermissions(
                this,
                "Это приложение требует доступ к хранилищу.",
                REQUEST_CODE_PERMISSIONS,
                *permissionsNeeded.toTypedArray()
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            Toast.makeText(this, "Все разрешения получены", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                Toast.makeText(
                    this,
                    "Разрешения заблокированы, включите их в настройках",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Разрешения отклонены", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }
}
