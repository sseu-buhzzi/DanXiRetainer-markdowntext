package dev.jeziellago.compose.markdowntext.plugins.image

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.graphics.withSave
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableLoader
import io.noties.markwon.image.ImageSize
import io.noties.markwon.image.ImageSizeResolver

class FixedHeightAsyncDrawable(
	val canvasHeight: Int,
	destination: String,
	loader: AsyncDrawableLoader,
	imageSizeResolver: ImageSizeResolver,
	imageSize: ImageSize?,
) : AsyncDrawable(
	destination,
	loader,
	imageSizeResolver,
	imageSize,
) {
	private var innerBounds: Rect? = null

	@Synchronized
	override fun setResult(result: Drawable) {
		innerBounds = null
		constrainResultInFixedHeight(result, lastKnownCanvasWidth)
		super.setResult(result)
	}

	@Synchronized
	override fun clearResult() {
		innerBounds = null
		super.clearResult()
	}

	@Synchronized
	override fun initWithKnownDimensions(width: Int, textSize: Float) {
		constrainResultInFixedHeight(result, width)
		super.initWithKnownDimensions(width, textSize)
	}

	override fun draw(canvas: Canvas) {
		result ?: return
		val canvasWidth = lastKnownCanvasWidth
		val innerBounds = innerBounds?.takeIf {
			canvasWidth > 0 && !it.isEmpty
		} ?: run {
			result.draw(canvas)
			return
		}
		val width = innerBounds.width()
		val height = innerBounds.height()
		canvas.withSave {
			val sx = width * canvasHeight / (height * canvasWidth).toFloat()
			if (sx <= 1) {
				val dx = canvasWidth * (1 - sx) / 2
				translate(dx, 0F)
				scale(sx, 1F)
			} else {
				val sy = 1 / sx
				val dy = canvasHeight * (1 - sy) / 2
				translate(0F, dy)
				scale(1F, sy)
			}
			result.draw(this)
		}
	}

	@Synchronized
	private fun constrainResultInFixedHeight(result: Drawable?, canvasWidth: Int) {
		result ?: return
		canvasWidth > 0 || return
		innerBounds?.let { return }
		innerBounds = Rect(result.bounds)
		result.setBounds(0, 0, canvasWidth, canvasHeight)
	}
}
