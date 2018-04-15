package com.zzzombiecoder.aboutanimation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zzzombiecoder.aboutanimation.collapsing.layout.CollapsingConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var collapsingLayout: CollapsingConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dataSource = DataSource(this)
        collapsingLayout = findViewById(R.id.constraint_layout)

        collapsingLayout.setUp(
                mainInfoItem = dataSource.getMainInfoItem(),
                infoItems = dataSource.getInfoItems(),
                onPhoneClickListener = { showToast("Phone clicked") },
                onInfoItemClickListener = { showToast("It is item: ${it.text}") })
    }
}
