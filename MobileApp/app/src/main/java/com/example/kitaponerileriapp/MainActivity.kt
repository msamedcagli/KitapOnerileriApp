package com.example.kitaponerileriapp

import androidx.navigation.navOptions
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.kitaponerileriapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var auth: FirebaseAuth

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Bildirimlere izin verildi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bildirim özelliği için izin gerekli.", Toast.LENGTH_LONG).show()
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        auth = FirebaseAuth.getInstance()

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener(this)

        val navController = findNavController(R.id.nav_host_fragment)
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            navController.navigate(R.id.homeFragment)
        } else {
            navController.navigate(R.id.loginFragment)
        }

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return@addCallback
            }

            when (navController.currentDestination?.id) {
                R.id.homeFragment -> {
                    finishAffinity()
                }
                else -> {
                    if (!navController.popBackStack()) {
                        finishAffinity()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        when(item.itemId) {

            R.id.nav_favorites -> {
                if (navController.currentDestination?.id == R.id.homeFragment) {
                    navController.navigate(R.id.action_homeFragment_to_favoritesFragment)
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.timer -> {
                if (navController.currentDestination?.id == R.id.homeFragment) {
                    navController.navigate(R.id.action_homeFragment_to_timerFragment)
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_logout -> {
                auth.signOut()
                Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()

                val navOptions = navOptions {
                    anim {
                        enter = R.anim.slide_in_left
                        exit = R.anim.slide_out_right
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_right
                    }
                    popUpTo(R.id.loginFragment) {
                        inclusive = true
                    }
                }

                navController.navigate(R.id.loginFragment, null, navOptions)

                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }
}
