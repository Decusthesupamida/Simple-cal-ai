package net.ppronko.pet.ai_calories_parser.data

data class MealParseInput(
    val description: String? = null,
    val imageUrl: String? = null,
    val imageFile: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MealParseInput

        if (description != other.description) return false
        if (imageUrl != other.imageUrl) return false
        if (imageFile != null) {
            if (other.imageFile == null) return false
            if (!imageFile.contentEquals(other.imageFile)) return false
        } else if (other.imageFile != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description?.hashCode() ?: 0
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (imageFile?.contentHashCode() ?: 0)
        return result
    }
}
