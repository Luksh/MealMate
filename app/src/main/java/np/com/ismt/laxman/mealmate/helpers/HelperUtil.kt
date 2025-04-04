package np.com.ismt.laxman.mealmate.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import np.com.ismt.laxman.mealmate.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HelperUtil {
    fun base64toBitmap(base64String: String, context: Context): Bitmap {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (exception: Exception) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_image_24)
            return drawable!!.toBitmap()
        }
    }

    fun showToastMessage(context: Context, message: String?) {
        (message?.isNotBlank() == true).let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun imageUriToBitmap(context: Context, imageUri: String): Bitmap {
        try {
            return MediaStore.Images.Media.getBitmap(
                context.applicationContext.contentResolver,
                Uri.parse(imageUri)
            )
        } catch (exception: Exception) {
            val drawable = ContextCompat.getDrawable(context, R.drawable.ic_image_24)
            return drawable!!.toBitmap()
        }
    }

    fun imageUriToBitmapEncodedBase64(context: Context, imageUri: String): String {
        val bitmap = imageUriToBitmap(context, imageUri)
        return bitmapToBase64(bitmap)
    }

    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}
