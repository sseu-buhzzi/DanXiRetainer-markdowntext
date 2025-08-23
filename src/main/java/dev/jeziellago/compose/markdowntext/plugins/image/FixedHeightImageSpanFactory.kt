package dev.jeziellago.compose.markdowntext.plugins.image

import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSpanFactory

class FixedHeightImageSpanFactory(val canvasHeight: Int) : ImageSpanFactory() {
	override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps) = AsyncDrawableSpan(
		configuration.theme(),
		FixedHeightAsyncDrawable(
			canvasHeight,
			ImageProps.DESTINATION.require(props),
			configuration.asyncDrawableLoader(),
			configuration.imageSizeResolver(),
			ImageProps.IMAGE_SIZE.get(props),
		),
		AsyncDrawableSpan.ALIGN_BOTTOM,
		ImageProps.REPLACEMENT_TEXT_IS_LINK.get(props, false),
	)
}
