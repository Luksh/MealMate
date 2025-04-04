package np.com.ismt.laxman.mealmate.helpers

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import np.com.ismt.laxman.mealmate.R

fun ImageView.loadThumbnailImage(context: Context, imageUriPath: String) {
    try {
        val bitmap = HelperUtil.imageUriToBitmap(context, imageUriPath)
        val adjustedBitmap = BitmapScalar.scaleToFill(
            bitmap,
            this.width,
            this.height
        )
        this.setImageBitmap(adjustedBitmap)
    } catch (exception: Exception) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_image_24)
        this.setImageBitmap(drawable!!.toBitmap())
    }
}
