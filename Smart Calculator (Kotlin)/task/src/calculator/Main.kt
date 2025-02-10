package calculator

fun main() {
    while (true) {
        val input = readln()
        when {
            input == "/exit" -> {
                println("Bye!")
                break
            }
            input == "/help" -> {
                println("The program calculates the sum of numbers")
                continue
            }
            input.isEmpty() -> continue
        }
        processInput(input)
    }
}

private fun processInput(input: String) {
    val nums = input.split(" ").mapNotNull { it.toIntOrNull() }
    when (nums.size) {
        in 2..1000 -> println(nums.sum())
        1 -> println(nums[0])
        else -> println("Not integers")
    }
}
