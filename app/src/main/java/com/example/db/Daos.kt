package com.example.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingListingDao {
    @Query("SELECT * FROM clothing_listings ORDER BY createdDate DESC")
    fun getAllListings(): Flow<List<ClothingListing>>

    @Query("SELECT * FROM clothing_listings WHERE ownerEmail = :ownerEmail ORDER BY createdDate DESC")
    fun getListingsByOwner(ownerEmail: String): Flow<List<ClothingListing>>

    @Query("SELECT * FROM clothing_listings WHERE id = :id")
    suspend fun getListingById(id: Int): ClothingListing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ClothingListing): Long

    @Update
    suspend fun updateListing(listing: ClothingListing)

    @Query("UPDATE clothing_listings SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("DELETE FROM clothing_listings WHERE id = :id")
    suspend fun deleteListingById(id: Int)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings WHERE renterEmail = :renterEmail ORDER BY createdDate DESC")
    fun getBookingsByRenter(renterEmail: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE ownerEmail = :ownerEmail ORDER BY createdDate DESC")
    fun getBookingsByOwner(ownerEmail: String): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE senderEmail = :email OR receiverEmail = :email ORDER BY timestamp DESC")
    fun getAllMessagesForUser(email: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE listingId = :listingId ORDER BY timestamp ASC")
    fun getMessagesForListing(listingId: Int): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long
}

@Dao
interface OfferDao {
    @Query("SELECT * FROM offers WHERE ownerEmail = :ownerEmail ORDER BY createdDate DESC")
    fun getOffersByOwner(ownerEmail: String): Flow<List<Offer>>

    @Query("SELECT * FROM offers WHERE renterEmail = :renterEmail ORDER BY createdDate DESC")
    fun getOffersByRenter(renterEmail: String): Flow<List<Offer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: Offer): Long

    @Query("UPDATE offers SET status = :status WHERE id = :id")
    suspend fun updateOfferStatus(id: Int, status: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE listingId = :listingId ORDER BY createdDate DESC")
    fun getReviewsForListing(listingId: Int): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long
}
