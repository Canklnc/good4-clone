package config

import com.good4.core.util.AppEnvironment
import com.good4.core.util.FirebaseBackend

/**
 * Features that are not wired to the V2 backend yet and therefore stay hidden
 * in builds that talk to good4tr-v2 (the store builds). Flip once ready.
 */
object ReleaseFeatures {
    private val isV2 get() = AppEnvironment.firebaseBackend == FirebaseBackend.V2

    /** Askıda Yemek still reads V1 products/codes; the V2 campaign flow has no app screen yet. */
    val suspendedMeals: Boolean get() = !isV2

    /** The in-app business and admin panels use V1 collections; V2 staff use the web panel. */
    val inAppStaffPanels: Boolean get() = !isV2

    const val WEB_PANEL_URL = "https://good4tr-v2.web.app"
}
