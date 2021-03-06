package teamkunle.co.uk.arcorelondroidtalk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        start_ar_experience.setOnClickListener{
            startARActivity()
        }

        start_avengers_experience.setOnClickListener {
            startAvengers()
        }
    }

    private fun startARActivity() {
        val intent = Intent(this, ARActivity::class.java)
        startActivity(intent)
    }

    private fun startAvengers() {
        val intent = Intent(this, AvengersAR::class.java)
        startActivity(intent)
    }
}
