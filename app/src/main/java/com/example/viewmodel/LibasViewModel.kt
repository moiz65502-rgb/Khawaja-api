package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class Screen {
    object LOGIN : Screen()
    object REGISTER : Screen()
    object HOME : Screen()
    object LISTING_DETAIL : Screen()
    object CREATE_LISTING : Screen()
    object DASHBOARD : Screen()
    object LOBBY : Screen()
    object CAREER : Screen()
    object PROFILE_SETTINGS : Screen()
    object APP_SETTINGS : Screen()
}

class LibasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LibasRepository
    
    // Auth & Profile state (default initialized to Abdul Moiz for instant seamless workspace sandbox)
    val currentUserEmail = MutableStateFlow<String>("moiz65502@gmail.com")
    val currentUserName = MutableStateFlow<String>("Abdul Moiz")
    val currentUserCnic = MutableStateFlow<String>("35201-8877665-3")
    val isDarkTheme = MutableStateFlow<Boolean>(true) // Matches visual aesthetic dark theme default

    // Custom backstack navigation state
    private val backstack = MutableStateFlow<List<Screen>>(listOf(Screen.HOME))
    val currentScreen: StateFlow<Screen> = backstack.map { it.lastOrNull() ?: Screen.HOME }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.HOME)

    // Listings selection & filtering
    val allListings = MutableStateFlow<List<ClothingListing>>(emptyList())
    val searchQuery = MutableStateFlow("")
    val categoryFilter = MutableStateFlow("All")
    val sizeFilter = MutableStateFlow("All")
    val locationFilter = MutableStateFlow("All")
    val deliveryFilter = MutableStateFlow("All")
    val typeFilter = MutableStateFlow("All") // "All", "Rent", "Sale"
    val maxPriceFilter = MutableStateFlow("any") // "any", "1000", "3000", "5000", "10000"

    // Wishlist matching local UI wishlist button
    val wishlistIds = MutableStateFlow<Set<Int>>(emptySet())

    // Active listing detail screen state
    val activeListingId = MutableStateFlow<Int?>(null)
    val activeListing = MutableStateFlow<ClothingListing?>(null)
    val activeListingReviews = MutableStateFlow<List<Review>>(emptyList())
    val activeListingMessages = MutableStateFlow<List<Message>>(emptyList())

    // Date range selection state
    val selectedDateFrom = MutableStateFlow<String?>(null)
    val selectedDateTo = MutableStateFlow<String?>(null)
    val paymentScreenshotPath = MutableStateFlow<String?>(null)
    val isUploadingScreenshot = MutableStateFlow(false)

    // Offers & Bookings active lists
    val myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val incomingBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myOffers = MutableStateFlow<List<Offer>>(emptyList())
    val myUserMessages = MutableStateFlow<List<Message>>(emptyList())

    // Creation form state
    val inputTitle = MutableStateFlow("")
    val inputDesc = MutableStateFlow("")
    val inputCategory = MutableStateFlow("Wedding")
    val inputSize = MutableStateFlow("M")
    val inputColor = MutableStateFlow("Red")
    val inputLocation = MutableStateFlow("Lahore")
    val inputDelivery = MutableStateFlow("Both")
    val inputType = MutableStateFlow("Rent") // "Rent", "Sale"
    val inputDailyRent = MutableStateFlow("")
    val inputSalePrice = MutableStateFlow("")
    val inputSecurityDeposit = MutableStateFlow("")
    val inputOwnerCnic = MutableStateFlow("")
    val isSavingListing = MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LibasRepository(
            clothingListingDao = database.clothingListingDao(),
            bookingDao = database.bookingDao(),
            messageDao = database.messageDao(),
            offerDao = database.offerDao(),
            reviewDao = database.reviewDao()
        )

        // Seed default listings if table is completely empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.allListings.first().let { currentList ->
                if (currentList.isEmpty()) {
                    SampleData.defaultListings.forEach {
                        repository.insertListing(it)
                    }
                }
            }
            
            // Gather real-time updates of listings
            launch {
                repository.allListings.collect {
                    allListings.value = it
                }
            }

            // Sync bookings/offers reactively for the logged in user
            launch {
                currentUserEmail.collect { email ->
                    if (email.isNotEmpty()) {
                        launch {
                            repository.getBookingsByRenter(email).collect { myBookings.value = it }
                        }
                        launch {
                            repository.getBookingsByOwner(email).collect { incomingBookings.value = it }
                        }
                        // For simplicity, offers are collected here as well
                        launch {
                            repository.getOffersByOwner(email).collect { myOffers.value = it }
                        }
                        launch {
                            repository.getAllMessagesForUser(email).collect { myUserMessages.value = it }
                        }
                    }
                }
            }
        }
    }

    // Navigation controllers
    fun navigateTo(screen: Screen) {
        val current = backstack.value.toMutableList()
        current.add(screen)
        backstack.value = current
    }

    fun navigateBack() {
        val current = backstack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            backstack.value = current
        }
    }

    fun logout() {
        currentUserEmail.value = ""
        currentUserName.value = ""
        currentUserCnic.value = ""
        backstack.value = listOf(Screen.LOGIN)
    }

    fun login(email: String, name: String) {
        currentUserEmail.value = email
        currentUserName.value = name
        backstack.value = listOf(Screen.HOME)
    }

    // Filtered lists
    val filteredListings: StateFlow<List<ClothingListing>> = combine(
        listOf(allListings, searchQuery, categoryFilter, sizeFilter, locationFilter, deliveryFilter, typeFilter, maxPriceFilter)
    ) { array ->
        val listings = array[0] as List<ClothingListing>
        val query = array[1] as String
        val category = array[2] as String
        val size = array[3] as String
        val location = array[4] as String
        val delivery = array[5] as String
        val type = array[6] as String
        val maxPrice = array[7] as String

        listings.filter { listing ->
            // Search query matches title or category or location (case-insensitive)
            val matchQuery = query.isEmpty() || 
                listing.title.contains(query, ignoreCase = true) ||
                listing.description.contains(query, ignoreCase = true) ||
                listing.category.contains(query, ignoreCase = true)
                
            val matchCategory = category == "All" || listing.category == category
            val matchSize = size == "All" || listing.size == size
            val matchLocation = location == "All" || listing.location.equals(location, ignoreCase = true)
            val matchDelivery = delivery == "All" || listing.deliveryMethod == delivery || listing.deliveryMethod == "Both"
            val matchType = type == "All" || listing.listingType == type
            
            val price = if (listing.listingType == "Sale") listing.salePrice else listing.dailyRent
            val maxPriceLimit = if (maxPrice == "any") Double.MAX_VALUE else (maxPrice.toDoubleOrNull() ?: Double.MAX_VALUE)
            val matchPrice = price <= maxPriceLimit
            
            matchQuery && matchCategory && matchSize && matchLocation && matchDelivery && matchType && matchPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist controllers
    fun toggleWishlist(listingId: Int) {
        val current = wishlistIds.value.toMutableSet()
        if (current.contains(listingId)) {
            current.remove(listingId)
        } else {
            current.add(listingId)
        }
        wishlistIds.value = current
    }

    // Select view detail listing
    fun selectListing(listingId: Int) {
        activeListingId.value = listingId
        viewModelScope.launch(Dispatchers.IO) {
            val item = repository.getListingById(listingId)
            activeListing.value = item
            
            if (item != null) {
                // Fetch reviews and messages for this listing
                launch {
                    repository.getReviewsForListing(listingId).collect {
                        activeListingReviews.value = it
                    }
                }
                launch {
                    repository.getMessagesForListing(listingId).collect {
                        activeListingMessages.value = it
                    }
                }
            }
        }
        navigateTo(Screen.LISTING_DETAIL)
    }

    // Submit a review locally
    fun submitReview(listingId: Int, listingTitle: String, rating: Int, comment: String, ownerEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val email = currentUserEmail.value
            val name = currentUserName.value
            val rev = Review(
                listingId = listingId,
                listingTitle = listingTitle,
                reviewerEmail = email,
                reviewerName = name,
                ownerEmail = ownerEmail,
                rating = rating,
                comment = comment
            )
            repository.insertReview(rev)
        }
    }

    // Send chat messages
    fun sendChatMessage(listingId: Int, listingTitle: String, receiverEmail: String, content: String) {
        if (content.trim().isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val email = currentUserEmail.value
            val name = currentUserName.value
            val msg = Message(
                listingId = listingId,
                listingTitle = listingTitle,
                senderEmail = email,
                senderName = name,
                receiverEmail = receiverEmail,
                content = content
            )
            repository.insertMessage(msg)
        }
    }

    // Submit offers locally
    fun submitOffer(listingId: Int, listingTitle: String, ownerEmail: String, offerPrice: Double, originalPrice: Double, msgText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val email = currentUserEmail.value
            val name = currentUserName.value
            val offer = Offer(
                listingId = listingId,
                listingTitle = listingTitle,
                renterEmail = email,
                renterName = name,
                ownerEmail = ownerEmail,
                offeredPrice = offerPrice,
                originalPrice = originalPrice,
                message = msgText
            )
            repository.insertOffer(offer)
        }
    }

    // Update Offer Status
    fun updateOfferStatus(offerId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateOfferStatus(offerId, status)
        }
    }

    // Upload payment screenshot helper (simulating real server integration)
    fun simulateScreenshotUpload(fileName: String) {
        viewModelScope.launch {
            isUploadingScreenshot.value = true
            kotlinx.coroutines.delay(1200) // Realistic upload delay
            paymentScreenshotPath.value = "mock_sc_${System.currentTimeMillis()}"
            isUploadingScreenshot.value = false
        }
    }

    // Create booking request
    fun submitBooking(listing: ClothingListing, startDate: String, endDate: String, totalDays: Int, totalRent: Double, platformFee: Double, screenshotName: String) {
        val email = currentUserEmail.value
        val name = currentUserName.value
        val screenshot = paymentScreenshotPath.value ?: "default_sc"
        
        viewModelScope.launch(Dispatchers.IO) {
            val b = Booking(
                listingId = listing.id,
                listingTitle = listing.title,
                renterEmail = email,
                renterName = name,
                ownerEmail = listing.ownerEmail,
                startDate = startDate,
                endDate = endDate,
                totalDays = totalDays,
                dailyRent = listing.dailyRent,
                totalRent = totalRent,
                platformFee = platformFee,
                securityDeposit = listing.securityDeposit,
                paymentScreenshot = screenshot,
                status = "pending"
            )
            repository.insertBooking(b)
            
            // Auto add the booked dates to unavailable dates list after renter places request
            val updatedUnavailableDates = (listing.unavailableDates + listOf(startDate, endDate)).distinct()
            repository.updateListing(listing.copy(unavailableDates = updatedUnavailableDates))
            
            // Clear date states
            selectedDateFrom.value = null
            selectedDateTo.value = null
            paymentScreenshotPath.value = null
        }
    }

    // Update booking requests
    fun updateBookingStatus(bookingId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBookingStatus(bookingId, status)
        }
    }

    // Add Listing locally (Posting an ad)
    fun submitNewListing() {
        val title = inputTitle.value.trim()
        val desc = inputDesc.value.trim()
        val category = inputCategory.value
        val size = inputSize.value
        val color = inputColor.value.trim()
        val location = inputLocation.value
        val delivery = inputDelivery.value
        val type = inputType.value
        val dailyRentStr = inputDailyRent.value
        val salePriceStr = inputSalePrice.value
        val securityStr = inputSecurityDeposit.value
        
        if (title.isEmpty() || desc.isEmpty()) return

        isSavingListing.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val email = currentUserEmail.value
            val name = currentUserName.value
            val cnic = currentUserCnic.value
            val rent = dailyRentStr.toDoubleOrNull() ?: 0.0
            val sale = salePriceStr.toDoubleOrNull() ?: 0.0
            val deposit = securityStr.toDoubleOrNull() ?: 0.0

            // Select matching unsplash high-quality generic image based on category for realistic visual catalog
            val categoryImage = when (category) {
                "Wedding" -> "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=500&fit=crop"
                "Mehndi" -> "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=500&fit=crop"
                "Walima" -> "https://images.unsplash.com/photo-1593032465175-481ac7f401a0?w=500&fit=crop"
                "Engagement" -> "https://images.unsplash.com/photo-1621184455862-c163dfb30e0f?w=500&fit=crop"
                "Casual" -> "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=500&fit=crop"
                else -> "https://images.unsplash.com/photo-1596783074918-c84cb06531ca?w=500&fit=crop"
            }

            val l = ClothingListing(
                listingType = type,
                title = title,
                description = desc,
                images = listOf(categoryImage),
                category = category,
                size = size,
                color = color,
                location = location,
                deliveryMethod = delivery,
                dailyRent = rent,
                salePrice = sale,
                securityDeposit = deposit,
                ownerName = name,
                ownerEmail = email,
                ownerCnic = cnic,
                status = "available"
            )
            repository.insertListing(l)

            // Clear inputs
            withContext(Dispatchers.Main) {
                inputTitle.value = ""
                inputDesc.value = ""
                inputDailyRent.value = ""
                inputSalePrice.value = ""
                inputSecurityDeposit.value = ""
                isSavingListing.value = false
                navigateBack()
            }
        }
    }
}
