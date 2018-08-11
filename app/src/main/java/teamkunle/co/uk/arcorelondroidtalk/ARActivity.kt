package teamkunle.co.uk.arcorelondroidtalk

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.ar.core.*
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_ar.*

class ARActivity : AppCompatActivity() {

    private lateinit var pointerDrawable : PointerDrawable
    private lateinit var fragment: ArFragment

    private var isTracking = false
    private var isHitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        initViews()
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun initViews() {
        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        fragment.arSceneView.scene.setOnUpdateListener{
           fragment.onUpdate(it)
            onUpdate()
        }

        pointerDrawable = PointerDrawable()
    }

    private fun onUpdate() {
        val trackingChanged: Boolean = updateTracking()
        val view: View = findViewById(android.R.id.content)

        if(trackingChanged) {
            if(isTracking) {
                view.overlay.add(pointerDrawable)
            } else {
                view.overlay.remove(pointerDrawable)
            }
            view.invalidate()
        }

        if(isTracking) {
            var hitTestChanged : Boolean = updateHitTest()
            if(hitTestChanged) {
                pointerDrawable.enabled = isHitting
                view.invalidate()
            }
        }

    }

    private fun updateTracking(): Boolean {
        val frame: Frame = fragment.arSceneView.arFrame

        var wasTracking: Boolean = isTracking
        isTracking = frame.camera.trackingState == TrackingState.TRACKING
        return isTracking != wasTracking
    }

    private fun updateHitTest(): Boolean {
        val frame: Frame = fragment.arSceneView.arFrame
        var pt = getScreenCenter()

        var hits: List<HitResult>
        var wasHitting : Boolean = isHitting
        isHitting = false

        frame.let{
            hits = it.hitTest(pt.x.toFloat(), pt.y.toFloat())

            hits.forEach{ i ->
                var trackable : Trackable = i.trackable

                if ((trackable is Plane) && ((trackable).isPoseInPolygon(i.hitPose))) {
                    isHitting = true
                    return@forEach
                }
            }
        }
        return wasHitting != isHitting
    }

    private fun getScreenCenter() : Point {
        var view: View = findViewById(android.R.id.content)
        return Point(view.width / 2, view.height / 2)
    }

    //initialise the gallery view
    private fun initGallery() {
        val gallery: LinearLayout = findViewById(R.id.gallery_layout)

        val andy  = ImageView(this)
        val cabin = ImageView(this)
        val house = ImageView(this)
        val igloo = ImageView(this)

        //droid
        andy.setImageResource(R.drawable.droid_thumb)
        andy.contentDescription = "andy"
        andy.setOnClickListener { addObject(Uri.parse("andy.sfb")) }
        gallery.addView(andy)

        //cabin
        cabin.setImageResource(R.drawable.cabin_thumb)
        cabin.contentDescription = "cabin"
        cabin.setOnClickListener { addObject(Uri.parse("Cabin.sfb")) }
        gallery.addView(cabin)

        //House
        house.setImageResource(R.drawable.house_thumb)
        house.contentDescription = "House"
        house.setOnClickListener{ addObject(Uri.parse("House.sfb"))}
        gallery.addView(house)

        //igloo
        igloo.setImageResource(R.drawable.igloo_thumb)
        igloo.contentDescription = "igloo"
        igloo.setOnClickListener { addObject(Uri.parse("igloo.sfb"))}
        gallery.addView(igloo)

    }

    //TODO : finish code
    private fun addObject(model: Uri?) {
        val frame = fragment.arSceneView.arFrame

    }
}
