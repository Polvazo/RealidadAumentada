package com.bbva.hackaton.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bbva.hackaton.R
import com.bbva.hackaton.fragment.NotificacionFragment
import com.bbva.hackaton.fragment.UbicacionFragment
import kotlinx.android.synthetic.main.activity_main.*








class MainActivity : AppCompatActivity() {

    var frameLayout: FrameLayout? = null
    var fragment: Fragment = NotificacionFragment()
    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null

    var initFragment = 2
    companion object {
        private const val ID_HOME = 1
        private const val ID_CAMARA = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frameLayout = findViewById<FrameLayout>(R.id.fragment_menu)

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.fragment_menu, fragment)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction!!.commit()

        btn_changed_view2.setOnClickListener(View.OnClickListener {

            when (initFragment){
                ID_HOME -> {
                    fragment = NotificacionFragment()
                    initFragment = 2
                }
                ID_CAMARA -> { fragment = UbicacionFragment()
                    initFragment = 1
                }

            }
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.replace(R.id.fragment_menu, fragment)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            ft.commit()
        })
    }



    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }


}