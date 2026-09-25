package com.good4.product.presentation.product_list.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.good4.config.domain.HomeBanner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.good4.core.presentation.AppBackground
import com.good4.core.presentation.BorderMuted
import com.good4.core.presentation.ErrorSnackbar
import com.good4.core.presentation.PistachioGreen
import com.good4.core.presentation.PrimaryGreen
import com.good4.core.presentation.SurfaceDefault
import com.good4.core.presentation.SurfaceCanvasWarm
import com.good4.core.presentation.SurfaceMuted
import com.good4.core.presentation.TextPrimary
import com.good4.core.presentation.TextSecondary
import com.good4.core.presentation.components.Good4NestedScaffold
import com.good4.core.presentation.components.toDisplayAddressOrNull
import com.good4.core.util.ReservationTimeCalculator
import com.good4.core.util.openMaps
import com.good4.dining.presentation.AkdenizDiningMenuCard
import com.good4.dining.presentation.AkdenizDiningMenuState
import com.good4.dining.presentation.AkdenizDiningMenuViewModel
import com.good4.feedback.FeedbackViewModel
import com.good4.product.Product
import com.good4.product.presentation.product_list.ProductListAction
import com.good4.product.presentation.product_list.ProductListState
import com.good4.product.presentation.product_list.ProductListViewModel
import good4.composeapp.generated.resources.Res
import good4.composeapp.generated.resources.home_delivery_time
import good4.composeapp.generated.resources.home_welcome_generic
import good4.composeapp.generated.resources.home_welcome_title
import good4.composeapp.generated.resources.good4_logo_transparent
import good4.composeapp.generated.resources.product_list_active_reservation_title
import good4.composeapp.generated.resources.product_list_countdown_prefix
import good4.composeapp.generated.resources.product_list_credit_label
import good4.composeapp.generated.resources.product_list_greeting_prefix
import good4.composeapp.generated.resources.product_list_greeting_suffix
import good4.composeapp.generated.resources.product_list_order_code_label
import good4.composeapp.generated.resources.reservation_status_pending
import good4.composeapp.generated.resources.student_reservations
import good4.composeapp.generated.resources.time_minute_suffix
import good4.composeapp.generated.resources.time_second_suffix
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun ProductListScreenRoot(
    communityManager: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = koinViewModel(),
    onProfileClick: (() -> Unit)? = null,
    onReservationCardClick: () -> Unit = {},
    onCommunitiesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onCampusMapClick: () -> Unit = {},
    onClassScheduleClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val diningMenuViewModel: AkdenizDiningMenuViewModel = koinViewModel()
    val diningMenuState by diningMenuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadActiveReservation()
        viewModel.loadStudentInfo()
        viewModel.loadHomeBanner()
        diningMenuViewModel.loadMenu()
    }

    ProductListScreen(
        communityManager = communityManager,
        modifier = modifier,
        state = state,
        diningMenuState = diningMenuState,
        onCommunitiesClick = onCommunitiesClick,
        onProfileClick = onProfileClick,
        onNotificationsClick = onNotificationsClick,
        onReservationCardClick = onReservationCardClick,
        onCalendarClick = onCalendarClick,
        onCampusMapClick = onCampusMapClick,
        onClassScheduleClick = onClassScheduleClick,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductListScreen(
    communityManager: Boolean = false,
    modifier: Modifier = Modifier,
    state: ProductListState,
    diningMenuState: AkdenizDiningMenuState = AkdenizDiningMenuState(),
    onProfileClick: (() -> Unit)? = null,
    onNotificationsClick: () -> Unit = {},
    onReservationCardClick: () -> Unit = {},
    onCommunitiesClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onCampusMapClick: () -> Unit = {},
    onClassScheduleClick: () -> Unit = {},
    onAction: (ProductListAction) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.activeReservation) {
        if (state.activeReservation != null) {
            listState.animateScrollToItem(0)
        }
    }

    Good4NestedScaffold(
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceCanvasWarm)
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryGreen
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = if (onProfileClick == null) {
                                WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
                            } else {
                                0.dp
                            },
                            bottom = 16.dp
                        )
                    ) {
                        stickyHeader {
                            ProductListGreetingHeader(
                                userName = state.userName.orEmpty(),
                                onProfileClick = onProfileClick,
                                onNotificationsClick = onNotificationsClick
                            )
                        }

                        item {
                            CampusSummaryCards(
                                diningMenuState = diningMenuState
                            )
                        }

                        state.homeBanner?.let { banner ->
                            item { HomeAdvertisementBanner(banner) }
                        }

                        item {
                            HomeQuickActions(
                                communityManager = communityManager,
                                onReservationsClick = onReservationCardClick,
                                onCommunitiesClick = onCommunitiesClick,
                                onCalendarClick = onCalendarClick,
                                onCampusMapClick = onCampusMapClick,
                                onClassScheduleClick = onClassScheduleClick
                            )
                        }

                        state.activeReservation?.let { reservation ->
                            item {
                                ProductListActiveReservationCard(
                                    reservationCode = reservation.code,
                                    product = reservation.product,
                                    expiryTime = reservation.expiryTime,
                                    codeId = reservation.codeId,
                                    onClick = onReservationCardClick,
                                    onExpired = { codeId ->
                                        onAction(ProductListAction.OnReservationExpired(codeId))
                                    }
                                )
                            }
                        }

                    }
                }
            }

            ErrorSnackbar(
                modifier = Modifier.align(Alignment.TopCenter),
                errorMessage = state.errorMessage,
                onDismiss = { onAction(ProductListAction.OnDismissError) }
            )

        }
    }
}

@Composable
private fun ProductListGreetingHeader(
    modifier: Modifier = Modifier,
    userName: String,
    onProfileClick: (() -> Unit)? = null,
    onNotificationsClick: () -> Unit = {}
) {
    val topInset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .background(SurfaceCanvasWarm)
            .padding(start = 18.dp, end = 14.dp, top = topInset),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.good4_logo_transparent),
                contentDescription = "Good4",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 42.dp, height = 44.dp)
            )
            Text(
                modifier = Modifier.weight(1f),
                text = if (userName.isBlank()) {
                    stringResource(Res.string.home_welcome_generic)
                } else {
                    stringResource(Res.string.home_welcome_title, userName)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceDefault,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, BorderMuted.copy(alpha = 0.24f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Bildirimler",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-7).dp, y = 7.dp)
                            .size(7.dp)
                            .background(Color(0xFFFFD54F), CircleShape)
                    )
                }
                if (onProfileClick != null) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SurfaceCanvasWarm),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onProfileClick) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Profil",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSummaryCards(
    modifier: Modifier = Modifier,
    remainingCredits: Int,
    deliveryTimeMinutes: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(116.dp),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryGreen,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.product_list_credit_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.78f)
                )
                Text(
                    text = remainingCredits.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .height(116.dp),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceDefault,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.home_delivery_time),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "$deliveryTimeMinutes dk",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun HomeAdvertisementBanner(
    banner: HomeBanner,
    feedbackViewModel: FeedbackViewModel = koinViewModel()
) {
    val uriHandler = LocalUriHandler.current
    val isGood4Placeholder = banner.advertiserName.trim().equals("Good4", ignoreCase = true)
    val feedbackState by feedbackViewModel.state.collectAsStateWithLifecycle()
    var showAdInfo by remember { mutableStateOf(false) }
    var showAdReport by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 14.dp)
            .aspectRatio(12f / 5f)
            .clip(RoundedCornerShape(18.dp))
            .then(if (!isGood4Placeholder && banner.targetUrl.isNotBlank()) Modifier.clickable {
                uriHandler.openUri(banner.targetUrl)
            } else Modifier)
    ) {
        if (isGood4Placeholder) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceMuted,
                border = BorderStroke(1.dp, BorderMuted.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(30.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Reklam alanı",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Sponsorlu içerikler burada gösterilir.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            AsyncImage(
                model = banner.imageUrl,
                contentDescription = banner.advertiserName.ifBlank { "Sponsorlu içerik" },
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.58f)
            ) {
                Text(
                    text = "Reklam",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.94f)
            ) {
                IconButton(onClick = { showAdInfo = true }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Reklam hakkında ve reklamı bildir",
                        tint = TextPrimary,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }
    }

    if (showAdInfo) {
        AlertDialog(
            onDismissRequest = { showAdInfo = false },
            title = { Text("Reklam hakkında") },
            text = {
                Text(
                    "Reklamveren: ${banner.advertiserName}\n\n" +
                        "Bu reklam Good4 yöneticisi tarafından incelenip ana sayfadaki genel reklam alanında yayımlanır."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    feedbackViewModel.startNew()
                    reportReason = ""
                    showAdInfo = false
                    showAdReport = true
                }) { Text("Reklamı bildir") }
            },
            dismissButton = {
                TextButton(onClick = { showAdInfo = false }) { Text("Kapat") }
            }
        )
    }

    if (showAdReport) {
        AlertDialog(
            onDismissRequest = {
                if (!feedbackState.isSubmitting) showAdReport = false
            },
            title = { Text(if (feedbackState.isSubmitted) "Bildirim gönderildi" else "Reklamı bildir") },
            text = {
                if (feedbackState.isSubmitted) {
                    Text("Bildirimin Good4 yönetim paneline ulaştı. Teşekkürler.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("${banner.advertiserName} reklamında uygunsuz veya yaşa uygun olmayan bir içerik gördüysen bize bildir.")
                        OutlinedTextField(
                            value = reportReason,
                            onValueChange = { if (it.length <= 1000) reportReason = it },
                            label = { Text("Sorunu açıkla") },
                            supportingText = { Text("En az 10 karakter yazmalısın.") },
                            minLines = 3,
                            maxLines = 5,
                            enabled = !feedbackState.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )
                        feedbackState.errorMessage?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {
                if (feedbackState.isSubmitted) {
                    TextButton(onClick = { showAdReport = false }) { Text("Tamam") }
                } else {
                    TextButton(
                        enabled = reportReason.trim().length >= 10 && !feedbackState.isSubmitting,
                        onClick = {
                            val reason = reportReason.trim()
                            if (reason.length < 10) return@TextButton
                            feedbackViewModel.onSubjectChange(
                                "Reklam bildirimi: ${banner.advertiserName.take(90)}"
                            )
                            feedbackViewModel.onMessageChange(
                                "Reklamveren: ${banner.advertiserName}\n" +
                                    "Yayın: ${banner.startsOn}–${banner.endsOn}\n" +
                                    "Bildirim: $reason"
                            )
                            feedbackViewModel.submit()
                        }
                    ) {
                        Text(if (feedbackState.isSubmitting) "Gönderiliyor…" else "Gönder")
                    }
                }
            },
            dismissButton = {
                if (!feedbackState.isSubmitted) {
                    TextButton(
                        enabled = !feedbackState.isSubmitting,
                        onClick = { showAdReport = false }
                    ) { Text("Vazgeç") }
                }
            }
        )
    }
}

@Composable
private fun HomeQuickActions(
    communityManager: Boolean,
    onReservationsClick: () -> Unit,
    onCommunitiesClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onCampusMapClick: () -> Unit,
    onClassScheduleClick: () -> Unit
) {
    val actions = listOf(
        HomeQuickAction(
            title = if (communityManager) "Topluluğu Yönet" else "Topluluklar",
            icon = Icons.Outlined.Groups,
            accent = Color(0xFF75D9BE),
            onClick = onCommunitiesClick
        ),
        HomeQuickAction(
            title = "Ders Programı",
            icon = Icons.Outlined.MenuBook,
            accent = Color(0xFF4B9FD1),
            onClick = onClassScheduleClick
        ),
        HomeQuickAction(
            title = "Kampüs Haritası",
            icon = Icons.Outlined.Map,
            accent = PrimaryGreen,
            onClick = onCampusMapClick
        ),
        HomeQuickAction(
            title = "Akademik Takvim",
            icon = Icons.Outlined.CalendarMonth,
            accent = Color(0xFFA58DEB),
            onClick = onCalendarClick
        ),
        HomeQuickAction(
            title = stringResource(Res.string.student_reservations),
            icon = Icons.Outlined.ShoppingCart,
            accent = Color(0xFF8CB7ED),
            onClick = onReservationsClick
        ).takeIf { config.ReleaseFeatures.suspendedMeals }
    ).filterNotNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        actions.forEach { action ->
            HomeQuickActionCard(
                modifier = Modifier.fillMaxWidth(),
                title = action.title,
                icon = action.icon,
                accent = action.accent,
                onClick = action.onClick
            )
        }
    }
}

private data class HomeQuickAction(
    val title: String,
    val icon: ImageVector,
    val accent: Color,
    val onClick: (() -> Unit)? = null
)

@Composable
private fun HomeQuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: (() -> Unit)?
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceDefault,
        border = BorderStroke(1.dp, BorderMuted.copy(alpha = 0.55f)),
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp))) {
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, end = 62.dp),
                fontSize = 17.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 8.dp)
                    .size(58.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 3.dp, y = 3.dp)
                        .size(48.dp)
                        .graphicsLayer(rotationZ = -5f)
                        .background(
                            color = TextPrimary.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(14.dp)
                        )
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .graphicsLayer(rotationZ = -5f),
                    shape = RoundedCornerShape(14.dp),
                    color = accent.copy(alpha = 0.24f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.88f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductListActiveReservationCard(
    modifier: Modifier = Modifier,
    reservationCode: String,
    product: Product,
    expiryTime: Instant?,
    codeId: String,
    onClick: () -> Unit = {},
    onExpired: (String) -> Unit
) {
    var remainingTime by remember { mutableStateOf("") }
    var isExpired by remember { mutableStateOf(false) }
    val minuteSuffix = stringResource(Res.string.time_minute_suffix)
    val secondSuffix = stringResource(Res.string.time_second_suffix)

    LaunchedEffect(expiryTime) {
        while (expiryTime != null && !isExpired) {
            val remainingSeconds = ReservationTimeCalculator.remainingSecondsUntilExpiry(
                expiresAtEpochSeconds = expiryTime.epochSeconds
            ) ?: break
            if (remainingSeconds <= 0) {
                isExpired = true
                onExpired(codeId)
                break
            }

            remainingTime = ReservationTimeCalculator.formatRemainingTimeFromExpiry(
                expiresAtEpochSeconds = expiryTime.epochSeconds,
                minuteSuffix = minuteSuffix,
                secondSuffix = secondSuffix,
                expiredLabel = ""
            ).orEmpty()

            delay(1.seconds)
        }
    }

    val displayAddress = toDisplayAddressOrNull(product.address)
    val mapsAddress = product.addressUrl.ifBlank { null }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = PistachioGreen,
        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
        shadowElevation = 3.dp
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceDefault,
            border = BorderStroke(1.dp, BorderMuted.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.product_list_active_reservation_title),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PistachioGreen
                    ) {
                        Text(
                            text = stringResource(Res.string.reservation_status_pending),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = BorderMuted.copy(alpha = 0.4f))

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceMuted),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.storeName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (displayAddress != null) {
                            Text(
                                text = displayAddress,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = if (mapsAddress != null) {
                                    Modifier.clickable { openMaps(mapsAddress) }
                                } else {
                                    Modifier
                                },
                                textDecoration = if (mapsAddress != null) TextDecoration.Underline else null
                            )
                        }
                    }
                }

                if (reservationCode.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(Res.string.product_list_order_code_label),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary.copy(alpha = 0.65f)
                            )
                            Text(
                                text = reservationCode,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = PrimaryGreen,
                                letterSpacing = 0.5.sp
                            )
                        }
                        if (remainingTime.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(Res.string.product_list_countdown_prefix) +
                                            remainingTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ProductListScreenPreview() {
    MaterialTheme {
        ProductListScreen(
            state = ProductListState(),
            onAction = {}
        )
    }
}
