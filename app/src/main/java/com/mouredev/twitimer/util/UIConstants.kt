package com.mouredev.twitimer.util

/**
 * Created by MoureDev by Brais Moure on 5/13/21.
 * www.mouredev.com
 */
object UIConstants {

    const val VIEW_OPACITY = 0.5f
    const val SHADOW_OPACITY = 0.2f
    const val LOGO_HEIGHT = 18

}

enum class FontType(val path: String) {
    LIGHT("font/inter_light.ttf"),
    REGULAR("font/inter_regular.ttf"),
    BOLD("font/inter_bold.ttf")
}

enum class FontSize(val size: Int) {
    TITLE(24),
    SUBTITLE(22),
    HEAD(18),
    SUBHEAD(16),
    BODY(15),
    BUTTON(14),
    CAPTION(12)
}

enum class Size(val size: Int) {
    NONE(0),
    EXTRA_SMALL(2),
    VERY_SMALL(4),
    SMALL(8),
    SMALL_MEDIUM(10),
    MEDIUM(16),
    MEDIUM_HALF_BIG(20),
    MEDIUM_BIG(24),
    BIG(32),
    VERY_BIG(40),
    GIGANT(80)
}