package calculator

fun main() {
    while (true) {
        val input = readln()
        when {
            input == "/exit" -> {
                println("Bye!")
                break
            }
            input.isEmpty() -> continue
        }
        processInput(input)
    }
}

private fun processInput(input: String) {
    val nums = input.split(" ").mapNotNull { it.toIntOrNull() }
    when (nums.size) {
        2 -> println(nums[0] + nums[1])
        1 -> println(nums[0])
        else -> println("Not integers")
    }
}
