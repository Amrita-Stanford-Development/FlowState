package com.example.flowstate.util

object QuotesProvider {
    private val quotes = listOf(
        "Focus is the gateway to thinking clearly." to "Deepak Chopra",
        "Concentration is the secret of strength." to "Ralph Waldo Emerson",
        "Where focus goes, energy flows." to "Tony Robbins",
        "The successful warrior is the average man, with laser-like focus." to "Bruce Lee",
        "One reason so few of us achieve what we truly want is that we never direct our focus." to "Tony Robbins",
        "Starve your distractions, feed your focus." to "Unknown",
        "Focus on being productive instead of busy." to "Tim Ferriss",
        "It's not always that we need to do more but rather that we need to focus on less." to "Nathan W. Morris",
        "The key to success is to focus our conscious mind on things we desire." to "Napoleon Hill",
        "Stay focused, go after your dreams and keep moving toward your goals." to "LL Cool J",
        "Lack of direction, not lack of time, is the problem." to "Zig Ziglar",
        "The more you focus, the more you can achieve." to "Unknown",
        "Focus is a matter of deciding what things you're not going to do." to "John Carmack",
        "Your focus determines your reality." to "George Lucas",
        "What you stay focused on will grow." to "Roy T. Bennett"
    )

    fun getRandomQuote(): Pair<String, String> {
        return quotes.random()
    }

    fun getDailyQuote(): Pair<String, String> {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return quotes[dayOfYear % quotes.size]
    }
}
