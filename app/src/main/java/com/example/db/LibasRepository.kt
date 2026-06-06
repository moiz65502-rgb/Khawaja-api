package com.example.db

import kotlinx.coroutines.flow.Flow

class LibasRepository(
    private val clothingListingDao: ClothingListingDao,
    private val bookingDao: BookingDao,
    private val messageDao: MessageDao,
    private val offerDao: OfferDao,
    private val reviewDao: ReviewDao
) {
    // Listings
    val allListings: Flow<List<ClothingListing>> = clothingListingDao.getAllListings()

    fun getListingsByOwner(ownerEmail: String): Flow<List<ClothingListing>> =
        clothingListingDao.getListingsByOwner(ownerEmail)

    suspend fun getListingById(id: Int): ClothingListing? =
        clothingListingDao.getListingById(id)

    suspend fun insertListing(listing: ClothingListing): Long =
        clothingListingDao.insertListing(listing)

    suspend fun updateListing(listing: ClothingListing) =
        clothingListingDao.updateListing(listing)

    suspend fun updateListingStatus(id: Int, status: String) =
        clothingListingDao.updateStatus(id, status)

    suspend fun deleteListingById(id: Int) =
        clothingListingDao.deleteListingById(id)

    // Bookings
    fun getBookingsByRenter(renterEmail: String): Flow<List<Booking>> =
        bookingDao.getBookingsByRenter(renterEmail)

    fun getBookingsByOwner(ownerEmail: String): Flow<List<Booking>> =
        bookingDao.getBookingsByOwner(ownerEmail)

    suspend fun insertBooking(booking: Booking): Long =
        bookingDao.insertBooking(booking)

    suspend fun updateBookingStatus(id: Int, status: String) =
        bookingDao.updateBookingStatus(id, status)

    suspend fun deleteBookingById(id: Int) =
        bookingDao.deleteBookingById(id)

    // Messages
    fun getAllMessagesForUser(email: String): Flow<List<Message>> =
        messageDao.getAllMessagesForUser(email)

    fun getMessagesForListing(listingId: Int): Flow<List<Message>> =
        messageDao.getMessagesForListing(listingId)

    suspend fun insertMessage(message: Message): Long =
        messageDao.insertMessage(message)

    // Offers
    fun getOffersByOwner(ownerEmail: String): Flow<List<Offer>> =
        offerDao.getOffersByOwner(ownerEmail)

    fun getOffersByRenter(renterEmail: String): Flow<List<Offer>> =
        offerDao.getOffersByRenter(renterEmail)

    suspend fun insertOffer(offer: Offer): Long =
        offerDao.insertOffer(offer)

    suspend fun updateOfferStatus(id: Int, status: String) =
        offerDao.updateOfferStatus(id, status)

    // Reviews
    fun getReviewsForListing(listingId: Int): Flow<List<Review>> =
        reviewDao.getReviewsForListing(listingId)

    suspend fun insertReview(review: Review): Long =
        reviewDao.insertReview(review)
}
