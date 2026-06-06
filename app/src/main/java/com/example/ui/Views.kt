package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.db.Booking
import com.example.db.ClothingListing
import com.example.db.Message
import com.example.db.Offer
import com.example.viewmodel.LibasViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LibasApp(viewModel: LibasViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("libas_scaffold"),
        bottomBar = {
            if (currentScreen != Screen.LOGIN && currentScreen != Screen.REGISTER) {
                LibasBottomNavBar(currentScreen = currentScreen, onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                })
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInVertically(animationSpec = spring(), initialOffsetY = { it / 4 }) + fadeIn() togetherWith
                            slideOutVertically(animationSpec = spring(), targetOffsetY = { -it / 6 }) + fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.LOGIN -> LoginScreen(viewModel)
                    Screen.REGISTER -> RegisterScreen(viewModel)
                    Screen.HOME -> HomeScreen(viewModel)
                    Screen.LISTING_DETAIL -> ListingDetailScreen(viewModel)
                    Screen.CREATE_LISTING -> CreateListingScreen(viewModel)
                    Screen.DASHBOARD -> DashboardScreen(viewModel)
                    Screen.LOBBY -> LobbyScreen(viewModel)
                    Screen.CAREER -> CareerHelpScreen(viewModel)
                    Screen.PROFILE_SETTINGS -> ProfileSettingsScreen(viewModel)
                    Screen.APP_SETTINGS -> AppSettingsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun LibasBottomNavBar(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onNavigate(Screen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        NavigationBarItem(
            selected = currentScreen == Screen.CREATE_LISTING,
            onClick = { onNavigate(Screen.CREATE_LISTING) },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Post Ad", tint = MaterialTheme.colorScheme.primary) },
            label = { Text("Post Ad", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.DASHBOARD,
            onClick = { onNavigate(Screen.DASHBOARD) },
            icon = { Icon(Icons.Default.List, contentDescription = "Dashboard") },
            label = { Text("My Ads", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.LOBBY,
            onClick = { onNavigate(Screen.LOBBY) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Lobby") },
            label = { Text("Lobby", fontSize = 11.sp) }
        )
    }
}

// 1. LOGIN SCREEN
@Composable
fun LoginScreen(viewModel: LibasViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("login_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Identity Brand
        Text(
            text = "Libas",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Pakistan's Elite Dress Rental Marketplace",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("login_email"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("login_password"),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        if (email.trim().isNotEmpty() && password.length >= 4) {
                            viewModel.login(email.trim(), email.substringBefore("@").replaceFirstChar { it.uppercase() })
                        } else {
                            errorMessage = "Please enter valid email and password"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        viewModel.login("moiz65502@gmail.com", "Abdul Moiz")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Skip directly to Demo Sandbox", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Don't have an account?", color = MaterialTheme.colorScheme.onBackground.copy(0.7f))
            Text(
                text = "Register Now",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { viewModel.navigateTo(Screen.REGISTER) }
            )
        }
    }
}

// 2. REGISTER SCREEN
@Composable
fun RegisterScreen(viewModel: LibasViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("register_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Join Libas and start renting premium styles today",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_name")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("reg_email")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("reg_password")
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            error = "All fields are required"
                        } else if (password != confirmPassword) {
                            error = "Passwords do not match"
                        } else {
                            viewModel.login(email.trim(), name.trim())
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("register_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register & Start", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Already have an account? ")
            Text(
                text = "Log In",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { viewModel.navigateBack() }
            )
        }
    }
}

// 3. HOME SCREEN (COLLECTION & BROWSE SECTIONS)
@Composable
fun HomeScreen(viewModel: LibasViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredListings by viewModel.filteredListings.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()

    var showFiltersExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Search Header Box with custom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Libas Marketplace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Pakistan's No.1 dress rental & sale",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.CAREER) },
                        modifier = Modifier.background(Color.White.copy(0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Helpline", tint = Color.White)
                    }
                }

                // Search Bar row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("search_field"),
                        placeholder = { Text("Search formal sherwanis, lehengas...", color = Color.White.copy(0.7f), fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(0.8f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(0.5f),
                            focusedContainerColor = Color.White.copy(0.1f),
                            unfocusedContainerColor = Color.White.copy(0.08f)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(26.dp)
                    )

                    IconButton(
                        onClick = { showFiltersExpanded = !showFiltersExpanded },
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                if (showFiltersExpanded) MaterialTheme.colorScheme.secondary else Color.White.copy(0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Filters",
                            tint = if (showFiltersExpanded) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        // Expanded Filters Pane
        if (showFiltersExpanded) {
            HomeFiltersPane(viewModel = viewModel) {
                showFiltersExpanded = false
            }
        }

        // Horizontal Category Chips List
        val categories = listOf(
            Triple("All", "🛍️", "All"),
            Triple("Wedding", "👰", "Wedding"),
            Triple("Mehndi", "🌿", "Mehndi"),
            Triple("Walima", "💍", "Walima"),
            Triple("Party", "🎉", "Party"),
            Triple("Engagement", "💎", "Engagement"),
            Triple("Casual", "👗", "Casual")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (label, emoji, category) ->
                val isSelected = categoryFilter == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.categoryFilter.value = category },
                    label = { Text("$emoji $label", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        // Main Listings Grid Section
        if (filteredListings.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary.copy(0.6f),
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = "No Listings Match Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "Adjust categories, size, or city filters above to browse other styles in Pakistan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                Button(
                    onClick = {
                        viewModel.searchQuery.value = ""
                        viewModel.categoryFilter.value = "All"
                        viewModel.sizeFilter.value = "All"
                        viewModel.locationFilter.value = "All"
                        viewModel.typeFilter.value = "All"
                        viewModel.maxPriceFilter.value = "any"
                    }
                ) {
                    Text("Clear All Filters")
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredListings) { item ->
                    ListingCardItem(
                        listing = item,
                        isFavorite = wishlistIds.contains(item.id),
                        onFavoriteClick = { viewModel.toggleWishlist(item.id) },
                        onCardClick = { viewModel.selectListing(item.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeFiltersPane(viewModel: LibasViewModel, onDone: () -> Unit) {
    val sizeF by viewModel.sizeFilter.collectAsState()
    val cityF by viewModel.locationFilter.collectAsState()
    val typeF by viewModel.typeFilter.collectAsState()
    val maxPF by viewModel.maxPriceFilter.collectAsState()

    val cities = listOf("All", "Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan")
    val sizes = listOf("All", "XS", "S", "M", "L", "XL", "XXL")
    val types = listOf(
        Pair("All", "All Types"),
        Pair("Rent", "Renting"),
        Pair("Sale", "For Purchase")
    )
    val priceLimits = listOf(
        Pair("any", "Any Price"),
        Pair("1000", "Under 1k"),
        Pair("3000", "Under 3k"),
        Pair("5000", "Under 5k"),
        Pair("10000", "Under 10k")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filters Pane", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    "Clear",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        viewModel.sizeFilter.value = "All"
                        viewModel.locationFilter.value = "All"
                        viewModel.typeFilter.value = "All"
                        viewModel.maxPriceFilter.value = "any"
                    }
                )
            }

            // City Filter
            Text("Select City", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                cities.forEach { city ->
                    val isSelected = cityF == city
                    SuggestionChip(
                        onClick = { viewModel.locationFilter.value = city },
                        label = { Text(city, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Size Filter
            Text("Select Size", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sizes.forEach { size ->
                    val isSelected = sizeF == size
                    SuggestionChip(
                        onClick = { viewModel.sizeFilter.value = size },
                        label = { Text(size, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Type filter
                Column(modifier = Modifier.weight(1f)) {
                    Text("Listing Type", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        types.forEach { (typeVal, typeLabel) ->
                            val isSelected = typeF == typeVal
                            SuggestionChip(
                                onClick = { viewModel.typeFilter.value = typeVal },
                                label = { Text(typeLabel, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
                
                // Max Price
                Column(modifier = Modifier.weight(1f)) {
                    Text("Max Price (PKR)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        priceLimits.forEach { (limitVal, limitLabel) ->
                            val isSelected = maxPF == limitVal
                            SuggestionChip(
                                onClick = { viewModel.maxPriceFilter.value = limitVal },
                                label = { Text(limitLabel, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Apply Filters", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ListingCardItem(
    listing: ClothingListing,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Main Listing Image with fallback
            AsyncImage(
                model = listing.images.firstOrNull(),
                contentDescription = listing.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Category Badges & Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = listing.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Sale Banner overlay
            if (listing.listingType == "Sale") {
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        "FOR SALE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Details
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val pricing = if (listing.listingType == "Sale") {
                    "Rs. ${listing.salePrice.toInt().toLocaleString()}"
                } else {
                    "Rs. ${listing.dailyRent.toInt().toLocaleString()}/day"
                }

                Text(
                    text = pricing,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "Size ${listing.size}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(11.dp), tint = Color.Gray)
                Text(
                    text = listing.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// 4. LISTING DETAIL SCREEN (SECURE LOCAL DATEPICKING & VERIFICATIONS)
@Composable
fun ListingDetailScreen(viewModel: LibasViewModel) {
    val listing by viewModel.activeListing.collectAsState()
    val reviews by viewModel.activeListingReviews.collectAsState()
    val chatMessages by viewModel.activeListingMessages.collectAsState()
    val dateFrom by viewModel.selectedDateFrom.collectAsState()
    val dateTo by viewModel.selectedDateTo.collectAsState()
    val uploadSC by viewModel.paymentScreenshotPath.collectAsState()
    val isUploading by viewModel.isUploadingScreenshot.collectAsState()
    val userEmail by viewModel.currentUserEmail.collectAsState()

    var showOfferDialog by remember { mutableStateOf(false) }
    var offerPrice by remember { mutableStateOf("") }
    var offerMsg by remember { mutableStateOf("") }

    var chatMessageText by remember { mutableStateOf("") }
    
    var reviewRating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    if (listing == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val item = listing!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("detail_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Listing Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = { viewModel.toggleWishlist(item.id) }) {
                    val wishlistIds by viewModel.wishlistIds.collectAsState()
                    Icon(
                        if (wishlistIds.contains(item.id)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (wishlistIds.contains(item.id)) Color.Red else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Image Gallery Display
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth().height(280.dp)
            ) {
                AsyncImage(
                    model = item.images.firstOrNull(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Title and Essential spec rows
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(item.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Text(item.location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Text("· Size ${item.size}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("· Color ${item.color}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }

        // Specs Highlights Box
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Daily Rent", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "Rs. ${item.dailyRent.toInt().toLocaleString()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Security (Refundable)", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            "Rs. ${item.securityDeposit.toInt().toLocaleString()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // Description Paragraph
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("About This Outfit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )
            }
        }

        // Seller Identification Trust status box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF2E7D32))
                        Text("Seller Quality Trust Box", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 14.sp)
                    }
                    Text(
                        "Owner: ${item.ownerName} (${item.ownerEmail})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Text("CNIC / ID Card Verified: Registered securely", fontSize = 11.sp, color = Color.DarkGray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Text("Active on Libas with 100% genuine products", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }
        }

        // Booking form / Transaction Section – ONLY for renter, not owner
        if (item.ownerEmail != userEmail) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("booking_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.15f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("📅 Pick Rental Dates & Book", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        // Date Pickers placeholders
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dateFrom ?: "",
                                onValueChange = { viewModel.selectedDateFrom.value = it },
                                modifier = Modifier.weight(1f).testTag("date_from_input"),
                                label = { Text("Start Date", fontSize = 11.sp) },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )

                            OutlinedTextField(
                                value = dateTo ?: "",
                                onValueChange = { viewModel.selectedDateTo.value = it },
                                modifier = Modifier.weight(1f).testTag("date_to_input"),
                                label = { Text("End Date", fontSize = 11.sp) },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 11.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }

                        // Calculate totals automatically
                        var isDateValid = false
                        var days = 0
                        var totalRent = 0.0
                        var platformFee = 0.0
                        var grandTotal = 0.0

                        try {
                            if (!dateFrom.isNullOrEmpty() && !dateTo.isNullOrEmpty()) {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                val d1 = sdf.parse(dateFrom!!)
                                val d2 = sdf.parse(dateTo!!)
                                if (d2 != null && d1 != null && d2.time >= d1.time) {
                                    val diff = d2.time - d1.time
                                    days = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
                                    totalRent = days * item.dailyRent
                                    platformFee = totalRent * 0.01
                                    grandTotal = totalRent + platformFee + item.securityDeposit
                                    isDateValid = true
                                }
                            }
                        } catch (e: Exception) {
                            isDateValid = false
                        }

                        if (isDateValid) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Rental Rent ($days days):", fontSize = 12.sp)
                                    Text("Rs. ${totalRent.toInt().toLocaleString()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Platform Fee (1% Secure):", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("Rs. ${platformFee.toInt().toLocaleString()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Security Deposit (Refundable):", fontSize = 12.sp, color = Color.Gray)
                                    Text("Rs. ${item.securityDeposit.toInt().toLocaleString()}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Grand Total Due:", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    Text("Rs. ${grandTotal.toInt().toLocaleString()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        // Meezan Bank details box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            border = BorderStroke(1.dp, Color(0xFFFBC02D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🏦 Secure escrow: Meezan Bank", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                                Text("A/C Name: Libas Escrow Pvt Ltd", fontSize = 11.sp, color = Color.Black)
                                Text("Account: 02840107819676", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color.Black)
                                Text("Transfer due total, take screenshot, and upload below.", fontSize = 10.sp, color = Color.DarkGray)
                            }
                        }

                        // Screenshot upload
                        Button(
                            onClick = { viewModel.simulateScreenshotUpload("screenshot.png") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (isUploading) "Uploading Screenshot..." else if (uploadSC != null) "✓ Screenshot Uploaded" else "Upload Payment Screenshot",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (isDateValid) {
                                    viewModel.submitBooking(
                                        listing = item,
                                        startDate = dateFrom!!,
                                        endDate = dateTo!!,
                                        totalDays = days,
                                        totalRent = totalRent,
                                        platformFee = platformFee,
                                        screenshotName = uploadSC ?: ""
                                    )
                                }
                            },
                            enabled = isDateValid && uploadSC != null,
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_booking_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Confirm Booking Request", fontWeight = FontWeight.Bold)
                        }

                        // Low Price Offer negotiating action
                        Button(
                            onClick = { showOfferDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Negotiate & Make an Offer (Kam Price)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Active Chat threads with Owner section
        if (item.ownerEmail != userEmail) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💬 Live chat thread with owner", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(chatMessages) { msg ->
                                val isMe = msg.senderEmail == userEmail
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                ) {
                                    Surface(
                                        color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.widthIn(max = 240.dp)
                                    ) {
                                        Text(
                                            text = msg.content,
                                            modifier = Modifier.padding(8.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                    Text(
                                        text = if (isMe) "Me" else msg.senderName,
                                        fontSize = 9.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatMessageText,
                                onValueChange = { chatMessageText = it },
                                modifier = Modifier.weight(1f).height(48.dp),
                                placeholder = { Text("Ask detail, availability...", fontSize = 12.sp) }
                            )
                            IconButton(
                                onClick = {
                                    if (chatMessageText.trim().isNotEmpty()) {
                                        viewModel.sendChatMessage(item.id, item.title, item.ownerEmail, chatMessageText.trim())
                                        chatMessageText = ""
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send")
                            }
                        }
                    }
                }
            }
        }

        // Reviews section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("User Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "${reviews.size} reviews",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Write Review
                if (item.ownerEmail != userEmail) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Post an outfit Review", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..5).forEach { star ->
                                    val isSelected = reviewRating >= star
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp).clickable { reviewRating = star },
                                        tint = if (isSelected) MaterialTheme.colorScheme.secondary else Color.LightGray
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = reviewComment,
                                onValueChange = { reviewComment = it },
                                label = { Text("Your rating comment...") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (reviewComment.trim().isNotEmpty()) {
                                        viewModel.submitReview(
                                            item.id,
                                            item.title,
                                            reviewRating,
                                            reviewComment,
                                            item.ownerEmail
                                        )
                                        reviewComment = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Post Review", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Reviews List
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    reviews.forEach { rev ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(rev.reviewerName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        (1..5).forEach { star ->
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (rev.rating >= star) MaterialTheme.colorScheme.secondary else Color.LightGray,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                                Text(rev.comment, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Negotiating Offer popup dialog
    if (showOfferDialog) {
        Dialog(onDismissRequest = { showOfferDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Negotiate Outfit offer price 👗", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text("Original price is Rs. ${item.dailyRent.toInt().toLocaleString()}/day. Suggest a discount below.", fontSize = 12.sp)

                    OutlinedTextField(
                        value = offerPrice,
                        onValueChange = { offerPrice = it },
                        label = { Text("Your proposed Daily rent") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = offerMsg,
                        onValueChange = { offerMsg = it },
                        label = { Text("Note for owner (e.g. event dates, self pickup...)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showOfferDialog = false }) { Text("Cancel") }
                        Button(
                            onClick = {
                                val price = offerPrice.toDoubleOrNull() ?: 0.0
                                if (price > 0.0) {
                                    viewModel.submitOffer(item.id, item.title, item.ownerEmail, price, item.dailyRent, offerMsg)
                                    showOfferDialog = false
                                    offerPrice = ""
                                    offerMsg = ""
                                }
                            }
                        ) {
                            Text("Send Offer")
                        }
                    }
                }
            }
        }
    }
}

// 5. CREATE LISTING (POST AN AD FORM DETAILED)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateListingScreen(viewModel: LibasViewModel) {
    val title by viewModel.inputTitle.collectAsState()
    val desc by viewModel.inputDesc.value.let { viewModel.inputDesc.collectAsState() }
    val category by viewModel.inputCategory.collectAsState()
    val size by viewModel.inputSize.collectAsState()
    val color by viewModel.inputColor.collectAsState()
    val location by viewModel.inputLocation.collectAsState()
    val delivery by viewModel.inputDelivery.collectAsState()
    val type by viewModel.inputType.collectAsState()
    val rent by viewModel.inputDailyRent.collectAsState()
    val price by viewModel.inputSalePrice.collectAsState()
    val security by viewModel.inputSecurityDeposit.collectAsState()
    val isSaving by viewModel.isSavingListing.collectAsState()

    val categories = listOf("Wedding", "Mehndi", "Party", "Walima", "Engagement", "Casual")
    val sizes = listOf("XS", "S", "M", "L", "XL", "XXL", "Custom")
    val cities = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan")
    val deliveryMethods = listOf("Self Pickup", "Home Delivery", "Both")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("create_listing_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post Dress Ad Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.inputTitle.value = it },
                label = { Text("Outfit Title (e.g. Designer Maroon Lehenga)") },
                modifier = Modifier.fillMaxWidth().testTag("add_item_title"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = desc,
                onValueChange = { viewModel.inputDesc.value = it },
                label = { Text("Details & Description of outfits (condition, material)") },
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("add_item_desc")
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Select Category", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.inputCategory.value = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Select Size", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    sizes.forEach { s ->
                        val isSelected = size == s
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.inputSize.value = s },
                            label = { Text(s, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = color,
                    onValueChange = { viewModel.inputColor.value = it },
                    label = { Text("Outfit Color") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text("Deals Type", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Rent", "Sale").forEach { option ->
                            val isSelected = type == option
                            SuggestionChip(
                                onClick = { viewModel.inputType.value = option },
                                label = { Text(option, fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Select City Location", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    cities.forEach { cit ->
                        val isSelected = location == cit
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.inputLocation.value = cit },
                            label = { Text(cit, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Delivery Method Offered", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    deliveryMethods.forEach { method ->
                        val isSelected = delivery == method
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.inputDelivery.value = method },
                            label = { Text(method, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (type == "Rent") {
                    OutlinedTextField(
                        value = rent,
                        onValueChange = { viewModel.inputDailyRent.value = it },
                        label = { Text("Daily Rent (Rs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("add_item_rent"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = security,
                        onValueChange = { viewModel.inputSecurityDeposit.value = it },
                        label = { Text("Refundable deposit") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { viewModel.inputSalePrice.value = it },
                        label = { Text("Sale Price (Rs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.submitNewListing() },
                enabled = !isSaving && title.isNotEmpty() && desc.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_item_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isSaving) "Posting Ad..." else "Publish Libas Ad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// 6. DASHBOARD SCREEN (ESCROWS, MY LISTINGS, OFFERS, INBOX CHATS)
@Composable
fun DashboardScreen(viewModel: LibasViewModel) {
    val myBookings by viewModel.myBookings.collectAsState()
    val incomingBookings by viewModel.incomingBookings.collectAsState()
    val myListings by viewModel.allListings.collectAsState()
    val myOffers by viewModel.myOffers.collectAsState()
    val myUserMessages by viewModel.myUserMessages.collectAsState()
    val userEmail by viewModel.currentUserEmail.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Listings, 1: Rentals (Spent), 2: Rented Out, 3: Offers, 4: Messages

    val filteredMyListings = myListings.filter { it.ownerEmail == userEmail }
    val rentedOutList = incomingBookings.filter { it.status == "confirmed" || it.status == "completed" }

    // Calc Escrow Wallets
    val grossEarned = rentedOutList.sumOf { it.totalRent }
    val secureEscrow = incomingBookings.filter { it.status == "pending" }.sumOf { it.securityDeposit }
    val netEarned = grossEarned * 0.99 // After 1% platform fee

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Dashboard", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Escrow Account & Rentals Manager", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        // Digital wallet
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                        Text("Libas Escrow Wallet", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Net Earnings", fontSize = 11.sp, color = Color.White.copy(0.7f))
                            Text("Rs. ${netEarned.toInt().toLocaleString()}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                            Text("after 1% platform fee", fontSize = 9.sp, color = Color.White.copy(0.5f))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Escrow Secured Hold", fontSize = 11.sp, color = Color.White.copy(0.7f))
                            Text("Rs. ${secureEscrow.toInt().toLocaleString()}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.secondary)
                            Text("held securely till return", fontSize = 9.sp, color = Color.White.copy(0.5f))
                        }
                    }
                }
            }
        }

        // Dashboard Tabs Row
        item {
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("My Ads") })
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("My Rentals") })
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Rented Out") })
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }, text = { Text("Offers") })
                Tab(selected = activeTab == 4, onClick = { activeTab = 4 }, text = { Text("Inbox Chats") })
            }
        }

        // Tab detail display
        when (activeTab) {
            0 -> {
                // My Listings
                if (filteredMyListings.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("You have not listed any dress yet.", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(filteredMyListings) { clothing ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectListing(clothing.id) }
                        ) {
                            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                AsyncImage(
                                    model = clothing.images.firstOrNull(),
                                    contentDescription = clothing.title,
                                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(clothing.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Category: ${clothing.category} · Size ${clothing.size}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Rs. ${clothing.dailyRent.toInt().toLocaleString()}/day", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        clothing.status.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // My Rentals Booked (Rent I ordered)
                if (myBookings.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("No rental bookings made yet", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(myBookings) { booking ->
                        BookingDetailRow(booking = booking, isIncoming = false, onApprove = {}, onDecline = {})
                    }
                }
            }
            2 -> {
                // Incoming Bookings (Rent requests for my dresses)
                if (incomingBookings.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("No incoming rent proposals yet", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(incomingBookings) { booking ->
                        val isPending = booking.status == "pending"
                        BookingDetailRow(
                            booking = booking,
                            isIncoming = true,
                            onApprove = {
                                viewModel.updateBookingStatus(booking.id, "confirmed")
                            },
                            onDecline = {
                                viewModel.updateBookingStatus(booking.id, "cancelled")
                            }
                        )
                    }
                }
            }
            3 -> {
                // Offers Received (Negotiated offer discount requests)
                if (myOffers.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("No discounted offers received", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    items(myOffers) { offer ->
                        OfferDetailRow(
                            offer = offer,
                            onAccept = {
                                viewModel.updateOfferStatus(offer.id, "accepted")
                            },
                            onDecline = {
                                viewModel.updateOfferStatus(offer.id, "declined")
                            }
                        )
                    }
                }
            }
            4 -> {
                // Inbox Messages
                val threadMessages = myUserMessages.groupBy { it.listingId }
                if (threadMessages.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("Inbox is empty", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    threadMessages.forEach { (listingId, list) ->
                        val lastMsg = list.first()
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectListing(listingId) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(lastMsg.listingTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${lastMsg.senderName}: ${lastMsg.content}", fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(
    booking: Booking,
    isIncoming: Boolean,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(booking.listingTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Surface(
                    color = when (booking.status) {
                        "confirmed" -> Color(0xFFC8E6C9)
                        "cancelled" -> Color(0xFFFFCDD2)
                        else -> Color(0xFFFFF9C4)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        booking.status.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = when (booking.status) {
                            "confirmed" -> Color(0xFF2E7D32)
                            "cancelled" -> Color(0xFFC62828)
                            else -> Color(0xFFF57F17)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text("Rent Term: ${booking.startDate} to ${booking.endDate} (${booking.totalDays} Days)", fontSize = 11.sp, color = Color.Gray)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Escrow Total Due: Rs. ${booking.totalRent.toInt().toLocaleString()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(if (isIncoming) "From: ${booking.renterName}" else "Owner email: ${booking.ownerEmail}", fontSize = 11.sp, color = Color.Gray)
            }

            if (isIncoming && booking.status == "pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDecline) {
                        Text("Decline Request", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onApprove) {
                        Text("Confirm Escrow Active")
                    }
                }
            }
        }
    }
}

@Composable
fun OfferDetailRow(
    offer: Offer,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(offer.listingTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Surface(
                    color = when (offer.status) {
                        "accepted" -> Color(0xFFC8E6C9)
                        "declined" -> Color(0xFFFFCDD2)
                        else -> Color(0xFFFFF9C4)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        offer.status.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = when (offer.status) {
                            "accepted" -> Color(0xFF2E7D32)
                            "declined" -> Color(0xFFC62828)
                            else -> Color(0xFFF57F17)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text("Price proposal: Rs. ${offer.offeredPrice.toInt().toLocaleString()}/day (Original was: ${offer.originalPrice.toInt().toLocaleString()}/day)", fontSize = 11.sp)
            if (offer.message.isNotEmpty()) {
                Text("Renter custom note: \"${offer.message}\"", fontSize = 11.sp, color = Color.Gray)
            }

            if (offer.status == "pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDecline) {
                        Text("Decline", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAccept) {
                        Text("Accept Proposal")
                    }
                }
            }
        }
    }
}

// 7. LOBBY / SETTINGS NAVIGATION SCREEN
@Composable
fun LobbyScreen(viewModel: LibasViewModel) {
    val email by viewModel.currentUserEmail.collectAsState()
    val name by viewModel.currentUserName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lobby_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Lobby Portal", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Access personal settings, help center, & custom preferences", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        // Profile brief
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (name.isNotEmpty()) name[0].uppercase() else "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(email, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Options list
            LobbyOptionItem(title = "Profile Identity Settings", details = "Edit name, contact phone, or owner CNIC identification", icon = Icons.Default.Person) {
                viewModel.navigateTo(Screen.PROFILE_SETTINGS)
            }
            LobbyOptionItem(title = "App theme preferences", details = "Toggle Dark mode / Light mode display settings", icon = Icons.Default.Settings) {
                viewModel.navigateTo(Screen.APP_SETTINGS)
            }
            LobbyOptionItem(title = "Customer Helpline Help Center", details = "Get help, payment verification support meezan guidelines", icon = Icons.Default.Call) {
                viewModel.navigateTo(Screen.CAREER)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout Session", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LobbyOptionItem(title: String, details: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(details, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

// 8. CAREER / CUSTOMER SERVICE SCREEN
@Composable
fun CareerHelpScreen(viewModel: LibasViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("career_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Libas Support Helpdesk", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Text("🙋 High-fidelity customer helpline support and services are live! Worn outfit, order conflict, bank transfers problem? Hum yahan hain aapke liye.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Support Helpline Active Numbers", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.clickable { }) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text("Helpline number:", fontSize = 11.sp, color = Color.Gray)
                        Text("+92 (325) 1121858", fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.clickable { }) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text("Email Support Address:", fontSize = 11.sp, color = Color.Gray)
                        Text("libasrent@gmail.com", fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.clickable { }) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text("Helpline Working Hours:", fontSize = 11.sp, color = Color.Gray)
                        Text("Mon-Sat: 9:00 AM - 9:00 PM (PKT)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Escrow Safety tips for Deals 🔒", fontWeight = FontWeight.Bold)
                Text("1. Kabhi bi product verify kye bina payments manual deliver na karein.", fontSize = 12.sp)
                Text("2. Use Room Secure database logs from dashboard to file dispute claims.", fontSize = 12.sp)
                Text("3. Keep record of bank transfers to Libas Escrow Meezan accounts.", fontSize = 12.sp)
            }
        }
    }
}

// 9. PROFILE SETTINGS SCREEN (PROPER MODEL WRITING)
@Composable
fun ProfileSettingsScreen(viewModel: LibasViewModel) {
    val name by viewModel.currentUserName.collectAsState()
    val email by viewModel.currentUserEmail.collectAsState()
    val cnic by viewModel.currentUserCnic.collectAsState()

    var inputName by remember { mutableStateOf(name) }
    var inputCnic by remember { mutableStateOf(cnic) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Text("Complete physical details below for seller ID verified badges.", fontSize = 12.sp, color = Color.Gray)

        OutlinedTextField(
            value = inputName,
            onValueChange = { inputName = it },
            label = { Text("App Display Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = {},
            label = { Text("Google Account linked email") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = inputCnic,
            onValueChange = { inputCnic = it },
            label = { Text("CNIC Identification No (for Verified badge)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.currentUserName.value = inputName
                viewModel.currentUserCnic.value = inputCnic
                viewModel.navigateBack()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Save Profile updates")
        }
    }
}

// 10. APP SETTINGS SCREEN (THEME MANAGED FROM LOCAL STATE ENGINE)
@Composable
fun AppSettingsScreen(viewModel: LibasViewModel) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_settings_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("App Settings Preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode / Night themes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Switch application background lighting theme", fontSize = 11.sp, color = Color.Gray)
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.isDarkTheme.value = it }
                    )
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Language", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("English & Urdu auto-language translation live", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text("Urdu (auto-detected)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// Helper utility extension to write currency formatting
fun Int.toLocaleString(): String {
    return "%,d".format(this)
}
