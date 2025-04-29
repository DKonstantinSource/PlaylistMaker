package com.example.playlistmaker.presentation.ui.host

import android.os.Bundle
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import pub.devrel.easypermissions.EasyPermissions
import com.example.playlistmaker.databinding.ActivityHostBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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


//    private fun createTestNotification() {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "channel_id",
//                "Test Channel",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(this, "channel_id")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("Тест уведомление")
//            .setContentText("Пробуем отправить уведомление")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        notificationManager.notify(1, notification)
//    }


    private fun requestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.POST_NOTIFICATIONS)) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            EasyPermissions.requestPermissions(
                this,
                "Это приложение требует доступ к хранилищу и уведомлениям для корректной работы.",
                REQUEST_CODE_PERMISSIONS,
                *permissionsNeeded.toTypedArray()
            )
        }
    }


    fun setBottomNavigationVisibility(isVisible: Boolean) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val divider = findViewById<View>(R.id.divider)

        if (isVisible) {
            bottomNavigationView.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
            divider.visibility = View.GONE
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            Toast.makeText(this, "Все разрешения получены", Toast.LENGTH_SHORT).show()
//            createTestNotification()
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
