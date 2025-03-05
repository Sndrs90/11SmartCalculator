package calculator

fun main() {
    while (true) {
        val input = readln()
        val regexCmd = "/\\w*".toRegex()
        when {
            input == "/exit" -> {
                println("Bye!")
                break
            }
            input == "/help" -> {
                println("The program calculates the sum of numbers")
                continue
            }
            regexCmd.matches(input) -> {
                println("Unknown command")
                continue
            }
            input.isEmpty() -> continue
        }
        processInput(input)
    }
}

fun normalizeOperators(expression: String): String {
    // Replace sequences of operators with their effective single operator
    return expression.replace(Regex("([+-])\\1+")) { match ->
        val length = match.value.length
        // Normalize to + for even counts and retain the sign for odd counts
        if (length % 2 == 0) "+" else match.value[0].toString()
    }.replace(Regex("\\+\\+"), "+") // Normalize any remaining ++ to +
}

fun parseExp(expression: String): Int {
    // Normalize the expression by collapsing multiple operators
    val normalizedExpression = normalizeOperators(expression.trim())

    // Split the expression using regex to match numbers and operators
    val regex = "([-+]?\\d+)".toRegex()
    val tokens = regex.findAll(normalizedExpression).map { it.value }.toList()
    val operators = normalizedExpression.split(regex).filter { it.isNotBlank() }.map { it.trim() }

    // Initialize the result with the first number
    var result = tokens.first().toInt()

    // Iterate through the tokens starting from the second one
    for (i in 1 until tokens.size) {
        val operator = operators[i - 1]  // Get the operator before the number
        val number = tokens[i].toInt()
        when (operator) {
            "+" -> result += number
            "-" -> result -= number
        }
    }
    return result
}

fun processInput(input: String) {
    val regex = Regex("^[\\s]*[-+]?\\d+(\\s*[-+]+\\s*[-+]?\\d+)*[\\s]*$")
    if (regex.matches(input)) {
        println(parseExp(input))
    } else {
        println("Invalid expression")
    }
}