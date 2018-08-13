package teamkunle.co.uk.arcorelondroidtalk

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.*
import java.util.concurrent.CompletableFuture

class ARActivity : AppCompatActivity() {

    private var pointerDrawable = PointerDrawableKT()
    private var fragment: ArFragment? = null

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

        fragment!!.arSceneView.scene.setOnUpdateListener{ frameTime ->
           fragment!!.onUpdate(frameTime)
            onUpdate()
        }

        initGallery()
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
        val frame: Frame = fragment!!.arSceneView.arFrame

        var wasTracking: Boolean = isTracking
        isTracking = frame.camera.trackingState == TrackingState.TRACKING
        return isTracking != wasTracking
    }

    private fun updateHitTest(): Boolean {
        val frame: Frame = fragment!!.arSceneView.arFrame
        var pt = getScreenCenter()

        var hits: List<HitResult>
        var wasHitting : Boolean = isHitting
        isHitting = false

        if (frame != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    isHitting = true
                    break
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
        andy.setOnClickListener {view -> addObject(Uri.parse("andy.sfb")) }
        gallery.addView(andy)

        //cabin
        cabin.setImageResource(R.drawable.cabin_thumb)
        cabin.contentDescription = "cabin"
        cabin.setOnClickListener { view -> addObject(Uri.parse("Cabin.sfb")) }
        gallery.addView(cabin)

        //House
        house.setImageResource(R.drawable.house_thumb)
        house.contentDescription = "House"
        house.setOnClickListener{view ->  addObject(Uri.parse("House.sfb"))}
        gallery.addView(house)

        //igloo
        igloo.setImageResource(R.drawable.igloo_thumb)
        igloo.contentDescription = "igloo"
        igloo.setOnClickListener {view ->  addObject(Uri.parse("igloo.sfb"))}
        gallery.addView(igloo)

    }

    //TODO : finish code
    private fun addObject(model: Uri) {
        val frame = fragment!!.arSceneView.arFrame
        val point = getScreenCenter()

        var hits: List<HitResult>

//        frame.let {
//            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
//
//            hits.forEach {
//                val trackable = it.trackable
//
//                if((trackable is Plane && (trackable).isPoseInPolygon(it.hitPose))){
//                    placeObject(fragment, it.createAnchor(), model)
//                    return@forEach
//                }
//            }
//        }

        frame.let {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for(hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(fragment!!, hit.createAnchor(), model)
                    break
                }
            }
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
        val renderableFuture: CompletableFuture<Void> = ModelRenderable.builder()
                .setSource(fragment?.context, model)
                .build()
                .thenAccept { renderable -> addNodeToScene(fragment, anchor, renderable)}
                .exceptionally { throwable ->
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(throwable.message)
                            .setTitle("Codelab error!")
                    val dialog = builder.create()
                    dialog.show()
                    null
                }
    }

    private fun addNodeToScene(fragment: ArFragment?, anchor: Anchor?, renderable: Renderable?) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment?.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment!!.arSceneView.scene.addChild(anchorNode)
        node.select()

    }

}
