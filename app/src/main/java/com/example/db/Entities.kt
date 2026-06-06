package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_listings")
data class ClothingListing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingType: String = "Rent", // "Rent", "Sale"
    val title: String,
    val description: String,
    val images: List<String> = emptyList(),
    val category: String, // "Wedding", "Mehndi", "Party", "Walima", "Engagement", "Casual"
    val size: String, // "XS", "S", "M", "L", "XL", "XXL", "Custom"
    val color: String = "",
    val location: String, // City
    val deliveryMethod: String, // "Self Pickup", "Home Delivery", "Both"
    val dailyRent: Double = 0.0,
    val salePrice: Double = 0.0,
    val securityDeposit: Double = 0.0,
    val ownerName: String = "",
    val ownerEmail: String,
    val ownerCnic: String = "",
    val status: String = "available", // "available", "rented", "sold", "paused"
    val unavailableDates: List<String> = emptyList(),
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val renterEmail: String,
    val renterName: String,
    val ownerEmail: String,
    val startDate: String, // "YYYY-MM-DD"
    val endDate: String, // "YYYY-MM-DD"
    val totalDays: Int,
    val dailyRent: Double,
    val totalRent: Double,
    val platformFee: Double,
    val securityDeposit: Double,
    val paymentScreenshot: String = "",
    val status: String = "pending", // "pending", "confirmed", "completed", "cancelled"
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val senderEmail: String,
    val senderName: String,
    val receiverEmail: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "offers")
data class Offer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val renterEmail: String,
    val renterName: String,
    val ownerEmail: String,
    val offeredPrice: Double,
    val originalPrice: Double,
    val message: String = "",
    val status: String = "pending", // "pending", "accepted", "declined"
    val createdDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val listingTitle: String,
    val reviewerEmail: String,
    val reviewerName: String,
    val ownerEmail: String,
    val rating: Int, // 1-5
    val comment: String,
    val createdDate: Long = System.currentTimeMillis()
)
