package tt.co.jesses.moonlight

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform