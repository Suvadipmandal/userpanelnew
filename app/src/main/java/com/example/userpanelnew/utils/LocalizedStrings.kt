package com.example.userpanelnew.utils

import com.example.userpanelnew.models.AppLanguage

object LocalizedStrings {
    
    fun getString(key: String, language: AppLanguage): String {
        return when (key) {
            "profile" -> when (language) {
                AppLanguage.ENGLISH -> "Profile"
                AppLanguage.HINDI -> "प्रोफाइल"
                AppLanguage.GUJARATI -> "પ્રોફાઇલ"
                AppLanguage.MARATHI -> "प्रोफाइल"
                AppLanguage.TELUGU -> "ప్రొఫైల్"
                AppLanguage.BENGALI -> "প্রোফাইল"
            }
            "settings" -> when (language) {
                AppLanguage.ENGLISH -> "Settings"
                AppLanguage.HINDI -> "सेटिंग्स"
                AppLanguage.GUJARATI -> "સેટિંગ્સ"
                AppLanguage.MARATHI -> "सेटिंग्ज"
                AppLanguage.TELUGU -> "సెట్టింగ్స్"
                AppLanguage.BENGALI -> "সেটিংস"
            }
            "language_settings" -> when (language) {
                AppLanguage.ENGLISH -> "Language Settings"
                AppLanguage.HINDI -> "भाषा सेटिंग्स"
                AppLanguage.GUJARATI -> "ભાષા સેટિંગ્સ"
                AppLanguage.MARATHI -> "भाषा सेटिंग्ज"
                AppLanguage.TELUGU -> "భాషా సెట్టింగ్స్"
                AppLanguage.BENGALI -> "ভাষা সেটিংস"
            }
            "select_language" -> when (language) {
                AppLanguage.ENGLISH -> "Select your preferred language:"
                AppLanguage.HINDI -> "अपनी पसंदीदा भाषा चुनें:"
                AppLanguage.GUJARATI -> "તમારી પસંદગીની ભાષા પસંદ કરો:"
                AppLanguage.MARATHI -> "तुमची आवडती भाषा निवडा:"
                AppLanguage.TELUGU -> "మీ ఇష్టమైన భాషను ఎంచుకోండి:"
                AppLanguage.BENGALI -> "আপনার পছন্দের ভাষা নির্বাচন করুন:"
            }
            "app_information" -> when (language) {
                AppLanguage.ENGLISH -> "App Information"
                AppLanguage.HINDI -> "ऐप की जानकारी"
                AppLanguage.GUJARATI -> "એપ્લિકેશનની માહિતી"
                AppLanguage.MARATHI -> "अॅप माहिती"
                AppLanguage.TELUGU -> "అప్లికేషన్ సమాచారం"
                AppLanguage.BENGALI -> "অ্যাপ তথ্য"
            }
            "version" -> when (language) {
                AppLanguage.ENGLISH -> "Version"
                AppLanguage.HINDI -> "वर्जन"
                AppLanguage.GUJARATI -> "વર્ઝન"
                AppLanguage.MARATHI -> "वर्जन"
                AppLanguage.TELUGU -> "వెర్షన్"
                AppLanguage.BENGALI -> "ভার্সন"
            }
            "build" -> when (language) {
                AppLanguage.ENGLISH -> "Build"
                AppLanguage.HINDI -> "बिल्ड"
                AppLanguage.GUJARATI -> "બિલ્ડ"
                AppLanguage.MARATHI -> "बिल्ड"
                AppLanguage.TELUGU -> "బిల్డ్"
                AppLanguage.BENGALI -> "বিল্ড"
            }
            "developer" -> when (language) {
                AppLanguage.ENGLISH -> "Developer"
                AppLanguage.HINDI -> "डेवलपर"
                AppLanguage.GUJARATI -> "ડેવલપર"
                AppLanguage.MARATHI -> "डेव्हलपर"
                AppLanguage.TELUGU -> "డెవలపర్"
                AppLanguage.BENGALI -> "ডেভেলপার"
            }
            "app_description" -> when (language) {
                AppLanguage.ENGLISH -> "A modern bus tracking application built with Jetpack Compose and Material 3 design."
                AppLanguage.HINDI -> "Jetpack Compose और Material 3 डिज़ाइन के साथ बनाया गया एक आधुनिक बस ट्रैकिंग एप्लिकेशन।"
                AppLanguage.GUJARATI -> "Jetpack Compose અને Material 3 ડિઝાઇન સાથે બનાવેલી એક આધુનિક બસ ટ્રેકિંગ એપ્લિકેશન।"
                AppLanguage.MARATHI -> "Jetpack Compose आणि Material 3 डिझाईनसह बनवलेली एक आधुनिक बस ट्रॅकिंग अॅप्लिकेशन।"
                AppLanguage.TELUGU -> "Jetpack Compose మరియు Material 3 డిజైన్‌తో నిర్మించబడిన ఆధునిక బస్సు ట్రాకింగ్ అప్లికేషన్."
                AppLanguage.BENGALI -> "Jetpack Compose এবং Material 3 ডিজাইন দিয়ে নির্মিত একটি আধুনিক বাস ট্র্যাকিং অ্যাপ্লিকেশন।"
            }
            "user_id" -> when (language) {
                AppLanguage.ENGLISH -> "User ID"
                AppLanguage.HINDI -> "यूजर आईडी"
                AppLanguage.GUJARATI -> "યુઝર આઈડી"
                AppLanguage.MARATHI -> "युजर आयडी"
                AppLanguage.TELUGU -> "యూజర్ ఐడీ"
                AppLanguage.BENGALI -> "ইউজার আইডি"
            }
            "email" -> when (language) {
                AppLanguage.ENGLISH -> "Email"
                AppLanguage.HINDI -> "ईमेल"
                AppLanguage.GUJARATI -> "ઈમેઇલ"
                AppLanguage.MARATHI -> "ईमेल"
                AppLanguage.TELUGU -> "ఇమెయిల్"
                AppLanguage.BENGALI -> "ইমেইল"
            }
            "phone" -> when (language) {
                AppLanguage.ENGLISH -> "Phone"
                AppLanguage.HINDI -> "फोन"
                AppLanguage.GUJARATI -> "ફોન"
                AppLanguage.MARATHI -> "फोन"
                AppLanguage.TELUGU -> "ఫోన్"
                AppLanguage.BENGALI -> "ফোন"
            }
            "logout" -> when (language) {
                AppLanguage.ENGLISH -> "Logout"
                AppLanguage.HINDI -> "लॉगआउट"
                AppLanguage.GUJARATI -> "લૉગઆઉટ"
                AppLanguage.MARATHI -> "लॉगआउट"
                AppLanguage.TELUGU -> "లాగ్అవుట్"
                AppLanguage.BENGALI -> "লগআউট"
            }
            "search_placeholder" -> when (language) {
                AppLanguage.ENGLISH -> "Search for bus or stop..."
                AppLanguage.HINDI -> "बस या स्टॉप खोजें..."
                AppLanguage.GUJARATI -> "બસ અથવા સ્ટોપ શોધો..."
                AppLanguage.MARATHI -> "बस किंवा स्टॉप शोधा..."
                AppLanguage.TELUGU -> "బస్సు లేదా స్టాప్ శోధించండి..."
                AppLanguage.BENGALI -> "বাস বা স্টপ খুঁজুন..."
            }
            "refresh" -> when (language) {
                AppLanguage.ENGLISH -> "Refresh"
                AppLanguage.HINDI -> "रिफ्रेश"
                AppLanguage.GUJARATI -> "રિફ્રેશ"
                AppLanguage.MARATHI -> "रिफ्रेश"
                AppLanguage.TELUGU -> "రిఫ్రెష్"
                AppLanguage.BENGALI -> "রিফ্রেশ"
            }
            "track_bus" -> when (language) {
                AppLanguage.ENGLISH -> "Track Bus"
                AppLanguage.HINDI -> "बस ट्रैक करें"
                AppLanguage.GUJARATI -> "બસ ટ્રેક કરો"
                AppLanguage.MARATHI -> "बस ट्रॅक करा"
                AppLanguage.TELUGU -> "బస్సును ట్రాక్ చేయండి"
                AppLanguage.BENGALI -> "বাস ট্র্যাক করুন"
            }
            "bus_tracked" -> when (language) {
                AppLanguage.ENGLISH -> "Bus tracked!"
                AppLanguage.HINDI -> "बस ट्रैक की गई!"
                AppLanguage.GUJARATI -> "બસ ટ્રેક થઈ!"
                AppLanguage.MARATHI -> "बस ट्रॅक झाली!"
                AppLanguage.TELUGU -> "బస్సు ట్రాక్ చేయబడింది!"
                AppLanguage.BENGALI -> "বাস ট্র্যাক করা হয়েছে!"
            }
            "nav_home" -> when (language) {
                AppLanguage.ENGLISH -> "Home"
                AppLanguage.HINDI -> "होम"
                AppLanguage.GUJARATI -> "હોમ"
                AppLanguage.MARATHI -> "होम"
                AppLanguage.TELUGU -> "హోమ్"
                AppLanguage.BENGALI -> "হোম"
            }
            "nav_stops" -> when (language) {
                AppLanguage.ENGLISH -> "Stops"
                AppLanguage.HINDI -> "स्टॉप्स"
                AppLanguage.GUJARATI -> "સ્ટોપ્સ"
                AppLanguage.MARATHI -> "स्टॉप्स"
                AppLanguage.TELUGU -> "స్టాప్స్"
                AppLanguage.BENGALI -> "স্টপস"
            }
            "nav_nearby_buses" -> when (language) {
                AppLanguage.ENGLISH -> "Nearby Buses"
                AppLanguage.HINDI -> "पास के बसें"
                AppLanguage.GUJARATI -> "નજીકની બસો"
                AppLanguage.MARATHI -> "जवळच्या बस"
                AppLanguage.TELUGU -> "సమీప బస్సులు"
                AppLanguage.BENGALI -> "কাছাকাছি বাস"
            }
            "nav_profile_settings" -> when (language) {
                AppLanguage.ENGLISH -> "Profile & Settings"
                AppLanguage.HINDI -> "प्रोफाइल और सेटिंग्स"
                AppLanguage.GUJARATI -> "પ્રોફાઇલ અને સેટિંગ્સ"
                AppLanguage.MARATHI -> "प्रोफाइल आणि सेटिंग्ज"
                AppLanguage.TELUGU -> "ప్రొఫైల్ మరియు సెట్టింగ్స్"
                AppLanguage.BENGALI -> "প্রোফাইল এবং সেটিংস"
            }
            else -> key // Fallback to key if not found
        }
    }
}






