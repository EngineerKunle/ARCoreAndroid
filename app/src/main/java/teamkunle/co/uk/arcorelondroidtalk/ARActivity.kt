package teamkunle.co.uk.arcorelondroidtalk

import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
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
        val trackingChanged : Boolean = updateTracking()
        val view : View = findViewById(android.R.id.content)

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
        val frame : Frame = fragment.arSceneView.arFrame

        var wasTracking: Boolean = isTracking
        isTracking = frame.camera.trackingState == TrackingState.TRACKING
        return isTracking != wasTracking
    }

    private fun updateHitTest(): Boolean {
        val frame : Frame = fragment.arSceneView.arFrame
        var pt = getScreenCenter()

        var hits : List<HitResult>
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

}
