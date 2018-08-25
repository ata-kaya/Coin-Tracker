package com.omidzamani.coinTracker.activity

import android.content.Intent
import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.omidzamani.coinTracker.fragment.CoinFragment

import com.omidzamani.coinTracker.R
import com.omidzamani.coinTracker.R.id.*
import com.omidzamani.coinTracker.fragment.CurrencyFragment
import com.omidzamani.coinTracker.utils.SharedPreference
import com.omidzamani.coinTracker.utils.Type
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import com.crashlytics.android.Crashlytics
import com.omidzamani.coinTracker.BuildConfig
import io.fabric.sdk.android.Fabric



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
        nav_view.setNavigationItemSelectedListener(this)


    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            reset_coins ->
                resetItems(Type.COIN.name.toLowerCase())
            reset_currency ->
                resetItems(Type.CURRENCY.name.toLowerCase())
            coin_edit ->
                startEditActivity(Type.COIN.name.toLowerCase())
            currency_edit ->
                startEditActivity(Type.CURRENCY.name.toLowerCase())
            about ->
                startActivity(Intent (Intent.ACTION_VIEW, Uri.parse("https://github.com/omid-zamani/Coin-Tracker")))

        }
        return true
    }

    private fun startEditActivity(itemName: String) {
        val intent = Intent(this, ListActivity::class.java)
        intent.putExtra("itemName", itemName)
        startActivity(intent)
    }

    private fun resetItems(itemName: String) {
        alert(String.format(getString(R.string.sure), itemName), getString(android.R.string.dialog_alert_title)) {
            yesButton {
                when (itemName) {
                    Type.COIN.name.toLowerCase() -> SharedPreference.getInstance(this@MainActivity).deleteAllCoins()
                    Type.CURRENCY.name.toLowerCase() -> SharedPreference.getInstance(this@MainActivity).deleteAllCurrencies()
                }
                longToast(String.format(getString(R.string.successfully_deleted), itemName))
            }
            noButton {

            }
        }.show()
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            if (position == 0)
                return CoinFragment.newInstance()
            return CurrencyFragment.newInstance()
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 2
        }
    }


}
