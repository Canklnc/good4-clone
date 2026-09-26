package com.good4.campus.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalAtm
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.good4.campus.domain.CampusPoint
import com.good4.campus.domain.CampusPointCategory
import com.good4.campus.domain.CampusPoints
import com.good4.core.presentation.PrimaryGreen
import com.good4.core.presentation.SurfaceDefault
import com.good4.core.presentation.TextPrimary
import com.good4.core.presentation.TextSecondary
import com.good4.core.presentation.components.Good4NestedScaffold
import com.good4.core.presentation.components.Good4TopBar
import good4.composeapp.generated.resources.Res
import good4.composeapp.generated.resources.campus_academic_pin
import good4.composeapp.generated.resources.campus_atm_pin
import good4.composeapp.generated.resources.campus_bank_pin
import good4.composeapp.generated.resources.campus_dormitory_pin
import good4.composeapp.generated.resources.campus_dining_pin
import good4.composeapp.generated.resources.campus_faculty_pin
import good4.composeapp.generated.resources.campus_library_pin
import good4.composeapp.generated.resources.campus_mosque_pin
import good4.composeapp.generated.resources.campus_shopping_pin
import good4.composeapp.generated.resources.campus_sports_pin
import good4.composeapp.generated.resources.campus_services_pin
import org.jetbrains.compose.resources.painterResource
import dev.sargunv.maplibrecompose.compose.ClickResult
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.compose.layer.SymbolLayer
import dev.sargunv.maplibrecompose.compose.rememberCameraState
import dev.sargunv.maplibrecompose.compose.rememberStyleState
import dev.sargunv.maplibrecompose.compose.source.rememberGeoJsonSource
import dev.sargunv.maplibrecompose.core.CameraPosition
import dev.sargunv.maplibrecompose.core.source.GeoJsonData
import dev.sargunv.maplibrecompose.expressions.dsl.const
import dev.sargunv.maplibrecompose.expressions.dsl.feature
import dev.sargunv.maplibrecompose.expressions.dsl.format
import dev.sargunv.maplibrecompose.expressions.dsl.image
import dev.sargunv.maplibrecompose.expressions.dsl.offset
import dev.sargunv.maplibrecompose.expressions.dsl.span
import dev.sargunv.maplibrecompose.expressions.dsl.asString
import dev.sargunv.maplibrecompose.expressions.value.SymbolAnchor
import io.github.dellisd.spatialk.geojson.Position

private const val OPEN_FREE_MAP_STYLE = "https://tiles.openfreemap.org/styles/liberty"
private val FacultyBlue = Color(0xFF3159A5)
private val AcademicPurple = Color(0xFF7048A8)
private val BankGold = Color(0xFFB77900)
private val AtmRed = Color(0xFFD64A3A)
private val ShoppingOrange = Color(0xFFE06B20)
private val MosqueTeal = Color(0xFF168A86)
private val SportsGreen = Color(0xFF2B8A4A)
private val DormitoryPink = Color(0xFFC33C78)
private val DiningBrown = Color(0xFF9C542C)

@Composable
fun CampusMapScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val library = CampusPoints.library
    var libraryVisible by rememberSaveable { mutableStateOf(true) }
    var facultiesVisible by rememberSaveable { mutableStateOf(true) }
    var academicUnitsVisible by rememberSaveable { mutableStateOf(true) }
    var serviceBuildingsVisible by rememberSaveable { mutableStateOf(true) }
    var diningHallsVisible by rememberSaveable { mutableStateOf(true) }
    var banksVisible by rememberSaveable { mutableStateOf(true) }
    var atmsVisible by rememberSaveable { mutableStateOf(true) }
    var shoppingAreasVisible by rememberSaveable { mutableStateOf(true) }
    var mosquesVisible by rememberSaveable { mutableStateOf(true) }
    var sportsAreasVisible by rememberSaveable { mutableStateOf(true) }
    var dormitoriesVisible by rememberSaveable { mutableStateOf(true) }
    var selectedPoints by remember { mutableStateOf(listOf(library)) }
    val libraryMarker = painterResource(Res.drawable.campus_library_pin)
    val facultyMarker = painterResource(Res.drawable.campus_faculty_pin)
    val academicMarker = painterResource(Res.drawable.campus_academic_pin)
    val serviceMarker = painterResource(Res.drawable.campus_services_pin)
    val diningMarker = painterResource(Res.drawable.campus_dining_pin)
    val bankMarker = painterResource(Res.drawable.campus_bank_pin)
    val atmMarker = painterResource(Res.drawable.campus_atm_pin)
    val shoppingMarker = painterResource(Res.drawable.campus_shopping_pin)
    val mosqueMarker = painterResource(Res.drawable.campus_mosque_pin)
    val sportsMarker = painterResource(Res.drawable.campus_sports_pin)
    val dormitoryMarker = painterResource(Res.drawable.campus_dormitory_pin)
    val libraryGeoJson = listOf(library).toGeoJson()
    val facultiesGeoJson = CampusPoints.faculties.toGeoJson()
    val academicUnitsGeoJson = CampusPoints.academicUnits.toGeoJson()
    val serviceBuildingsGeoJson = CampusPoints.serviceBuildings.toGeoJson()
    val diningHallsGeoJson = CampusPoints.diningHalls.toGeoJson()
    val banksGeoJson = CampusPoints.banks.toGeoJson()
    val atmsGeoJson = CampusPoints.atms.toGeoJson()
    val shoppingAreasGeoJson = CampusPoints.shoppingAreas.toGeoJson()
    val mosquesGeoJson = CampusPoints.mosques.toGeoJson()
    val sportsAreasGeoJson = CampusPoints.sportsAreas.toGeoJson()
    val dormitoriesGeoJson = CampusPoints.dormitories.toGeoJson()

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(latitude = 36.8965, longitude = 30.6585),
            zoom = 13.4
        )
    )
    val styleState = rememberStyleState()

    Good4NestedScaffold(
        modifier = modifier,
        topBar = {
            Good4TopBar(
                title = "Kampüs Haritası",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MaplibreMap(
                styleUri = OPEN_FREE_MAP_STYLE,
                cameraState = cameraState,
                styleState = styleState,
                onMapClick = { _, _ ->
                    selectedPoints = emptyList()
                    ClickResult.Pass
                },
                modifier = Modifier.fillMaxSize()
            ) {
                if (libraryVisible) {
                    val librarySource = rememberGeoJsonSource(
                        id = "campus-library",
                        data = GeoJsonData.JsonString(libraryGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-library-pin",
                        source = librarySource,
                        iconImage = image(libraryMarker),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
                if (facultiesVisible) {
                    val facultiesSource = rememberGeoJsonSource(
                        id = "campus-faculties",
                        data = GeoJsonData.JsonString(facultiesGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-faculty-pins",
                        source = facultiesSource,
                        iconImage = image(facultyMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                    SymbolLayer(
                        id = "campus-faculty-labels",
                        source = facultiesSource,
                        textField = format(span(feature.get("name").asString())),
                        textFont = const(listOf("Noto Sans Regular")),
                        textSize = const(10.sp),
                        textColor = const(Color(0xFF17324D)),
                        textHaloColor = const(Color.White),
                        textHaloWidth = const(1.5.dp),
                        textAnchor = const(SymbolAnchor.Top),
                        textOffset = offset(0.em, 0.45.em),
                        textMaxWidth = const(13.em),
                        textAllowOverlap = const(true),
                        textIgnorePlacement = const(true)
                    )
                }
                if (academicUnitsVisible) {
                    val academicUnitsSource = rememberGeoJsonSource(
                        id = "campus-academic-units",
                        data = GeoJsonData.JsonString(academicUnitsGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-academic-unit-pins",
                        source = academicUnitsSource,
                        iconImage = image(academicMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                    SymbolLayer(
                        id = "campus-academic-unit-labels",
                        source = academicUnitsSource,
                        textField = format(span(feature.get("name").asString())),
                        textFont = const(listOf("Noto Sans Regular")),
                        textSize = const(10.sp),
                        textColor = const(Color(0xFF35204F)),
                        textHaloColor = const(Color.White),
                        textHaloWidth = const(1.5.dp),
                        textAnchor = const(SymbolAnchor.Top),
                        textOffset = offset(0.em, 0.45.em),
                        textMaxWidth = const(13.em),
                        textAllowOverlap = const(true),
                        textIgnorePlacement = const(true)
                    )
                }
                if (serviceBuildingsVisible) {
                    val serviceBuildingsSource = rememberGeoJsonSource(
                        id = "campus-service-buildings",
                        data = GeoJsonData.JsonString(serviceBuildingsGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-service-building-pins",
                        source = serviceBuildingsSource,
                        iconImage = image(serviceMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                    SymbolLayer(
                        id = "campus-service-building-labels",
                        source = serviceBuildingsSource,
                        textField = format(span(feature.get("name").asString())),
                        textFont = const(listOf("Noto Sans Regular")),
                        textSize = const(10.sp),
                        textColor = const(Color(0xFF0D5D5A)),
                        textHaloColor = const(Color.White),
                        textHaloWidth = const(1.5.dp),
                        textAnchor = const(SymbolAnchor.Top),
                        textOffset = offset(0.em, 0.45.em),
                        textMaxWidth = const(13.em),
                        textAllowOverlap = const(true),
                        textIgnorePlacement = const(true)
                    )
                }
                if (diningHallsVisible) {
                    val diningHallsSource = rememberGeoJsonSource(
                        id = "campus-dining-halls",
                        data = GeoJsonData.JsonString(diningHallsGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-dining-hall-pins",
                        source = diningHallsSource,
                        iconImage = image(diningMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                    SymbolLayer(
                        id = "campus-dining-hall-labels",
                        source = diningHallsSource,
                        textField = format(span(feature.get("name").asString())),
                        textFont = const(listOf("Noto Sans Regular")),
                        textSize = const(10.sp),
                        textColor = const(Color(0xFF65351C)),
                        textHaloColor = const(Color.White),
                        textHaloWidth = const(1.5.dp),
                        textAnchor = const(SymbolAnchor.Top),
                        textOffset = offset(0.em, 0.45.em),
                        textMaxWidth = const(13.em),
                        textAllowOverlap = const(true),
                        textIgnorePlacement = const(true)
                    )
                }
                if (banksVisible) {
                    val banksSource = rememberGeoJsonSource(
                        id = "campus-banks",
                        data = GeoJsonData.JsonString(banksGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-bank-pins",
                        source = banksSource,
                        iconImage = image(bankMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
                if (atmsVisible) {
                    val atmsSource = rememberGeoJsonSource(
                        id = "campus-atms",
                        data = GeoJsonData.JsonString(atmsGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-atm-pins",
                        source = atmsSource,
                        iconImage = image(atmMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
                if (shoppingAreasVisible) {
                    val shoppingAreasSource = rememberGeoJsonSource(
                        id = "campus-shopping-areas",
                        data = GeoJsonData.JsonString(shoppingAreasGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-shopping-area-pins",
                        source = shoppingAreasSource,
                        iconImage = image(shoppingMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
                if (mosquesVisible) {
                    val mosquesSource = rememberGeoJsonSource(
                        id = "campus-mosques",
                        data = GeoJsonData.JsonString(mosquesGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-mosque-pins",
                        source = mosquesSource,
                        iconImage = image(mosqueMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
                if (sportsAreasVisible) {
                    val sportsAreasSource = rememberGeoJsonSource(
                        id = "campus-sports-areas",
                        data = GeoJsonData.JsonString(sportsAreasGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-sports-area-pins",
                        source = sportsAreasSource,
                        iconImage = image(sportsMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
                if (dormitoriesVisible) {
                    val dormitoriesSource = rememberGeoJsonSource(
                        id = "campus-dormitories",
                        data = GeoJsonData.JsonString(dormitoriesGeoJson)
                    )
                    SymbolLayer(
                        id = "campus-dormitory-pins",
                        source = dormitoriesSource,
                        iconImage = image(dormitoryMarker),
                        iconAnchor = const(SymbolAnchor.Bottom),
                        iconAllowOverlap = const(true),
                        onClick = { features ->
                            selectedPoints = features.selectedCampusPoints()
                            ClickResult.Consume
                        }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                CampusFilterChip(
                    selected = libraryVisible,
                    onClick = {
                        libraryVisible = !libraryVisible
                        if (!libraryVisible && selectedPoints.any { it.category == CampusPointCategory.LIBRARY }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Kütüphane · 1",
                    icon = { Icon(Icons.Outlined.LocalLibrary, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = facultiesVisible,
                    onClick = {
                        facultiesVisible = !facultiesVisible
                        if (!facultiesVisible && selectedPoints.any { it.category == CampusPointCategory.FACULTY }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Fakülteler · ${CampusPoints.faculties.size}",
                    icon = { Icon(Icons.Outlined.School, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = academicUnitsVisible,
                    onClick = {
                        academicUnitsVisible = !academicUnitsVisible
                        if (!academicUnitsVisible && selectedPoints.any { it.category == CampusPointCategory.ACADEMIC_UNIT }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Akademik Birimler · ${CampusPoints.academicUnits.size}",
                    icon = { Icon(Icons.Outlined.School, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = serviceBuildingsVisible,
                    onClick = {
                        serviceBuildingsVisible = !serviceBuildingsVisible
                        if (!serviceBuildingsVisible && selectedPoints.any { it.category == CampusPointCategory.SERVICE_BUILDING }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Hizmet Binaları · ${CampusPoints.serviceBuildings.size}",
                    icon = { Icon(Icons.Outlined.AccountBalance, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = diningHallsVisible,
                    onClick = {
                        diningHallsVisible = !diningHallsVisible
                        if (!diningHallsVisible && selectedPoints.any { it.category == CampusPointCategory.DINING_HALL }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Yemekhaneler · ${CampusPoints.diningHalls.size}",
                    icon = { Icon(Icons.Outlined.Restaurant, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = banksVisible,
                    onClick = {
                        banksVisible = !banksVisible
                        if (!banksVisible && selectedPoints.any { it.category == CampusPointCategory.BANK }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Bankalar · ${CampusPoints.banks.size}",
                    icon = { Icon(Icons.Outlined.AccountBalance, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = atmsVisible,
                    onClick = {
                        atmsVisible = !atmsVisible
                        if (!atmsVisible && selectedPoints.any { it.category == CampusPointCategory.ATM }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "ATM'ler · ${CampusPoints.atms.size}",
                    icon = { Icon(Icons.Outlined.LocalAtm, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = shoppingAreasVisible,
                    onClick = {
                        shoppingAreasVisible = !shoppingAreasVisible
                        if (!shoppingAreasVisible && selectedPoints.any { it.category == CampusPointCategory.SHOPPING }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Çarşılar · ${CampusPoints.shoppingAreas.size}",
                    icon = { Icon(Icons.Outlined.Storefront, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = mosquesVisible,
                    onClick = {
                        mosquesVisible = !mosquesVisible
                        if (!mosquesVisible && selectedPoints.any { it.category == CampusPointCategory.MOSQUE }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Cami · ${CampusPoints.mosques.size}",
                    icon = { Icon(Icons.Outlined.Mosque, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = sportsAreasVisible,
                    onClick = {
                        sportsAreasVisible = !sportsAreasVisible
                        if (!sportsAreasVisible && selectedPoints.any { it.category == CampusPointCategory.SPORTS }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "Spor Alanları · ${CampusPoints.sportsAreas.size}",
                    icon = { Icon(Icons.Outlined.SportsSoccer, contentDescription = null) }
                )
                CampusFilterChip(
                    selected = dormitoriesVisible,
                    onClick = {
                        dormitoriesVisible = !dormitoriesVisible
                        if (!dormitoriesVisible && selectedPoints.any { it.category == CampusPointCategory.DORMITORY }) {
                            selectedPoints = emptyList()
                        }
                    },
                    label = "KYK Yurtları · ${CampusPoints.dormitories.size}",
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null) }
                )
            }

            if (selectedPoints.isNotEmpty()) {
                CampusPointInfoCard(
                    points = selectedPoints,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CampusFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: @Composable () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = icon,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = SurfaceDefault,
            labelColor = TextSecondary,
            iconColor = TextSecondary,
            selectedContainerColor = SurfaceDefault,
            selectedLabelColor = PrimaryGreen,
            selectedLeadingIconColor = PrimaryGreen
        )
    )
}

private fun List<CampusPoint>.toGeoJson(): String {
    val features = joinToString(separator = ",") { point ->
        """
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": [${point.longitude}, ${point.latitude}]
              },
              "properties": {
                "name": "${point.name}",
                "category": "${point.category.name.lowercase()}"
              }
            }
        """.trimIndent()
    }
    return """{"type":"FeatureCollection","features":[$features]}"""
}

private fun List<io.github.dellisd.spatialk.geojson.Feature>.selectedCampusPoints(): List<CampusPoint> {
    val selectedNames = mapNotNull { feature -> feature.getStringProperty("name") }.distinct()
    return selectedNames.mapNotNull { name -> CampusPoints.all.firstOrNull { point -> point.name == name } }
}

@Composable
private fun CampusPointInfoCard(
    points: List<CampusPoint>,
    modifier: Modifier = Modifier
) {
    val firstPoint = points.first()
    val icon = when (firstPoint.category) {
        CampusPointCategory.LIBRARY -> Icons.Outlined.LocalLibrary
        CampusPointCategory.FACULTY, CampusPointCategory.ACADEMIC_UNIT -> Icons.Outlined.School
        CampusPointCategory.SERVICE_BUILDING -> Icons.Outlined.AccountBalance
        CampusPointCategory.DINING_HALL -> Icons.Outlined.Restaurant
        CampusPointCategory.BANK -> Icons.Outlined.AccountBalance
        CampusPointCategory.ATM -> Icons.Outlined.LocalAtm
        CampusPointCategory.SHOPPING -> Icons.Outlined.Storefront
        CampusPointCategory.MOSQUE -> Icons.Outlined.Mosque
        CampusPointCategory.SPORTS -> Icons.Outlined.SportsSoccer
        CampusPointCategory.DORMITORY -> Icons.Outlined.Home
    }
    val accentColor = when (firstPoint.category) {
        CampusPointCategory.LIBRARY -> PrimaryGreen
        CampusPointCategory.FACULTY -> FacultyBlue
        CampusPointCategory.ACADEMIC_UNIT -> AcademicPurple
        CampusPointCategory.SERVICE_BUILDING -> MosqueTeal
        CampusPointCategory.DINING_HALL -> DiningBrown
        CampusPointCategory.BANK -> BankGold
        CampusPointCategory.ATM -> AtmRed
        CampusPointCategory.SHOPPING -> ShoppingOrange
        CampusPointCategory.MOSQUE -> MosqueTeal
        CampusPointCategory.SPORTS -> SportsGreen
        CampusPointCategory.DORMITORY -> DormitoryPink
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDefault),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                points.forEach { point ->
                    Text(
                        text = point.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = if (points.size == 1) firstPoint.category.label else "${points.size} ${firstPoint.category.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
