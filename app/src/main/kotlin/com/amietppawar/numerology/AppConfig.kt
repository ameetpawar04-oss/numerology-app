package com.amietppawar.numerology

/**
 * EDIT LATER: All user-facing configuration for the numerology app.
 * Change these values to customize the app without modifying code.
 */

object AppConfig {

    // ========================================================================
    // EDIT LATER: Practice Details
    // ========================================================================

    const val PRACTICE_NAME = "Amiet Ppawar Numerology"
    const val CONSULTANT_NAME = "Amiet Ppawar"
    const val CONSULTANT_LOCATION = "Mumbai, India"
    const val CONSULTANT_BIO = "Professional Chaldean numerologist with 50+ consultations delivered. " +
        "Background in engineering with 19+ years in enterprise systems. " +
        "Using Chaldean numerology to reveal patterns in names and dates for clarity and self-discovery."

    // ========================================================================
    // EDIT LATER: Consultation Packages
    // ========================================================================

    data class Package(
        val id: String,
        val name: String,
        val priceINR: Int,
        val durationMinutes: Int,
        val description: String,
        val features: List<String>
    )

    val PACKAGES = listOf(
        Package(
            id = "clarity",
            name = "Name Clarity Session",
            priceINR = 10000,
            durationMinutes = 45,
            description = "Name number analysis and spelling guidance",
            features = listOf(
                "Your full name analysis",
                "Letter-by-letter breakdown",
                "Spelling alternatives and their vibrations",
                "Written summary"
            )
        ),
        Package(
            id = "complete",
            name = "Complete Chart Reading",
            priceINR = 15000,
            durationMinutes = 75,
            description = "Name, birth and destiny numbers with written summary",
            features = listOf(
                "Full name analysis",
                "Birth date numerology",
                "Destiny and life path insights",
                "How numbers interact in your chart",
                "Written summary with key takeaways"
            )
        ),
        Package(
            id = "signature",
            name = "Signature Consultation",
            priceINR = 25000,
            durationMinutes = 120,
            description = "Full reading, name correction options and one follow-up call",
            features = listOf(
                "Complete chart analysis",
                "Alternative name options if desired",
                "Life direction and timing insights",
                "Detailed written report",
                "One follow-up call within 30 days"
            )
        )
    )

    // ========================================================================
    // EDIT LATER: Contact & Booking
    // ========================================================================

    // Leave empty to hide buttons. Format: 919999999999 (country code, no +)
    const val WHATSAPP_NUMBER = ""

    // Leave empty to hide email fallback
    const val SUPPORT_EMAIL = ""

    // Optional: scheduling system link (e.g., Calendly)
    const val SCHEDULING_LINK = ""

    // Optional: payment link (e.g., Razorpay)
    const val PAYMENT_LINK = ""

    // Optional: Amazon book link
    const val AMAZON_BOOK_LINK = ""

    // Optional: social media links
    const val INSTAGRAM_URL = ""
    const val LINKEDIN_URL = ""
    const val TWITTER_URL = ""
    const val FACEBOOK_URL = ""

    // ========================================================================
    // EDIT LATER: Booking Details
    // ========================================================================

    const val BOOKING_ONLINE_AVAILABILITY = "Monday to Friday, 10 AM to 6 PM IST"
    const val BOOKING_INPERSON_LOCATION = "Mumbai (exact address shared on booking)"
    const val BOOKING_INPERSON_AVAILABILITY = "By appointment"

    // ========================================================================
    // EDIT LATER: Book (Coming Soon)
    // ========================================================================

    const val BOOK_TITLE = "Coming Soon"
    const val BOOK_DESCRIPTION = "A new book by Amiet Ppawar"
    const val BOOK_STATUS = "Publishing soon on Amazon KDP"

    // ========================================================================
    // EDIT LATER: Legal & Disclaimers
    // ========================================================================

    const val DISCLAIMER = "Numerology is a belief-based practice offered for guidance and self-reflection. " +
        "It is not a substitute for medical, legal, financial or psychological advice. " +
        "Outcomes are not guaranteed."

    const val PRIVACY_POLICY_SUMMARY = "This app does not collect or store any personal data on servers. " +
        "Your name, date of birth and consultation preferences are stored only on your device. " +
        "When you submit a booking request, the data is sent to WhatsApp or email as you direct. " +
        "We do not track your usage, show ads or use analytics."

    // ========================================================================
    // EDIT LATER: Number Meanings (Chaldean)
    // Replace with your own interpretations
    // ========================================================================

    val NUMBER_MEANINGS = mapOf(
        1 to "Independence, leadership, originality and new beginnings. Pioneers create new paths.",
        2 to "Balance, partnership, sensitivity and harmony. Diplomats bring people together.",
        3 to "Creativity, self-expression, communication and joy. Communicators inspire others.",
        4 to "Stability, foundation, hard work and dedication. Builders create lasting structures.",
        5 to "Freedom, change, adaptability and adventure. Explorers embrace new experiences.",
        6 to "Love, responsibility, nurturing and compassion. Caregivers support others with grace.",
        7 to "Introspection, wisdom, spirituality and analysis. Seekers understand deeper truths.",
        8 to "Abundance, power, material success and balance. Manifestors create prosperity.",
        9 to "Completion, humanitarianism, universal love and letting go. Healers serve humanity."
    )

    // ========================================================================
    // EDIT LATER: Daily number notification text
    // ========================================================================

    const val DAILY_NOTIFICATION_TITLE = "Today's number"

    // ========================================================================
    // THEME & COLOR PALETTE (Edit in UI theme files instead)
    // ========================================================================
    // The Number Cosmos palette:
    // Space Black-Indigo: #07060F
    // Midnight Violet: #1A1140
    // Electric Violet: #7B5CFF
    // Cyan Glow: #3EE6FF
    // Antique Gold: #D9B45A
    // White: #FFFFFF
    // (See ui/theme/Theme.kt)

    // ========================================================================
    // FEATURE FLAGS
    // ========================================================================

    const val ENABLE_DAILY_NOTIFICATIONS = true
    const val ENABLE_SAVED_PROFILES = true
    const val ENABLE_SHARE_RESULTS = true

    // Automatically reduce particle counts on devices with less than 2GB RAM
    const val AUTO_OPTIMIZE_PARTICLES = true
}
