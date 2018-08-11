package teamkunle.co.uk.arcorelondroidtalk

import android.graphics.*
import android.graphics.drawable.Drawable

class PointerDrawable : Drawable() {

    private val paint: Paint = Paint()
    var enabled: Boolean = false

    override fun draw(canvas: Canvas?) {
        val cx = canvas!!.width / 2f
        val cy = canvas!!.height / 2f

        if (enabled) {
            paint.color = Color.BLUE
            canvas!!.drawCircle(cx, cy, 10f, paint)
        } else {
            paint.color = Color.RED
            canvas!!.drawText("X", cx, cy, paint)
        }
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
}