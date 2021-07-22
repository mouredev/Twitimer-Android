package com.mouredev.twitimer.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Matrix.ScaleToFit
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.util.concurrent.TimeUnit


/**
 * Created by MoureDev by Brais Moure on 5/30/21.
 * www.mouredev.com
 */
object UIUtil {

    // Alert

    fun showAlert(context: Context, title: String, message: String, positive: String, positiveAction: (() -> Unit)? = null, negative: String? = null) {

        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positive) { _, _ ->
            positiveAction?.let {
                it()
            }
        }
        negative?.let {
            builder.setNegativeButton(it) { _, _ ->
                // Do nothing
            }
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.light))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.dark))
    }

    fun loadAvatar(context: Context?, url: String?, user: String?, imageView: ImageView) {

        url?.let {

            Picasso.get()
                .load(Uri.parse(url))
                .transform(CropCircleTransformation())
                .placeholder(R.drawable.user)
                .into(object : Target {

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        imageView.imageTintList = null

                        context?.let { context ->
                            //imageView.background = ContextCompat.getDrawable(context, R.drawable.shadow_oval_dark)
                            bitmap?.let {
                                val margin = Util.dpToPixel(context, 2)
                                val shadow = addShadow(bitmap, bitmap.height, bitmap.width, context.getColor(R.color.dark_shadow), 2, margin, margin)
                                imageView.setImageBitmap(shadow)
                            }
                        }
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        // Do nothing
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Do nothing
                    }
                })

            context?.let { context ->
                imageView.setOnLongClickListener {
                    Util.easteregg(context, user ?: "")
                    true
                }
            }
        }
    }

    // Private

    fun addShadow(bm: Bitmap, dstHeight: Int, dstWidth: Int, color: Int, size: Int, dx: Float, dy: Float): Bitmap? {

        val mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8)
        val scaleToFit = Matrix()
        val src = RectF(0f, 0f, bm.width.toFloat(), bm.height.toFloat())
        val dst = RectF(0f, 0f, dstWidth - dx, dstHeight - dy)
        scaleToFit.setRectToRect(src, dst, ScaleToFit.CENTER)
        val dropShadow = Matrix(scaleToFit)
        dropShadow.postTranslate(dx, dy)
        val maskCanvas = Canvas(mask)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskCanvas.drawBitmap(bm, scaleToFit, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        maskCanvas.drawBitmap(bm, dropShadow, paint)
        val filter = BlurMaskFilter(size.toFloat(), Blur.NORMAL)
        paint.reset()
        paint.isAntiAlias = true
        paint.color = color
        paint.maskFilter = filter
        paint.isFilterBitmap = true
        val ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val retCanvas = Canvas(ret)
        retCanvas.drawBitmap(mask, 0f, 0f, paint)
        retCanvas.drawBitmap(bm, scaleToFit, null)
        mask.recycle()
        return ret
    }

}