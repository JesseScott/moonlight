package tt.co.jesses.moonlight

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Guess what it is! > ${platform.name}!" +
                "\nThere are only ${daysUntilNewYear()} days left until New Year! 🎆"
    }
}