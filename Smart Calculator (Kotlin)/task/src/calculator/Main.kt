package calculator

fun main() {
    val variablesStorage = mutableMapOf<String, Int>()
    while (true) {
        val input = readln().trim()
        val regexExp = "[+-]".toRegex()
        val regexVar = "\\b[a-zA-Z]+(?=\\s*=?)".toRegex()
        val regCmd = "/+".toRegex()
        when {
            input == "/exit" -> {
                println("Bye!")
                break
            }
            input == "/help" -> {
                println("The program calculates the sum of numbers")
                continue
            }
            regCmd.containsMatchIn(input) -> {
                println("Unknown command")
                continue
            }
            input.isEmpty() -> continue
            regexExp.containsMatchIn(input) -> {
                processExpression(input, variablesStorage)
                continue // Continue to the next iteration
            }
            regexVar.containsMatchIn(input) -> {
                processVar(input, variablesStorage)
                continue // Continue to the next iteration
            }
        }
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

fun processExpression(input: String, variablesStorage: MutableMap<String, Int>) {
    // Normalize the input expression
    val normalizedExpression = input.replace("\\s+".toRegex(), " ").trim()

    // Split the expression into components while keeping operators
    val regex = "([-+]?\\d+|[a-zA-Z]+|[-+])".toRegex()
    val tokens = regex.findAll(normalizedExpression).map { it.value }.toList()

    // Resolve variables and numbers, and create a new list of resolved tokens
    val resolvedTokens = mutableListOf<String>()
    for (token in tokens) {
        when {
            token.matches(Regex("-?\\d+")) -> resolvedTokens.add(token) // it's a number
            variablesStorage.containsKey(token) -> resolvedTokens.add(variablesStorage[token]!!.toString()) // it's a variable
            else -> resolvedTokens.add(token) // it's an operator or unknown variable
        }
    }

    // Now we create a new expression from resolved tokens to pass to the parser
    val resolvedExpression = resolvedTokens.joinToString(" ")
    println(parseExp(resolvedExpression))
}

fun processVar(input: String, variablesStorage: MutableMap<String, Int>) {
    when {
        // Match a variable assignment with an integer
        "[a-zA-Z]+\\s*=\\s*\\d+".toRegex().matches(input) -> {
            val partsVar = input.replace("\\s".toRegex(), "").split("=")
            variablesStorage[partsVar[0]] = partsVar[1].toInt()
        }
        // Match a variable assignment with another variable
        "[a-zA-Z]+\\s*=\\s*[a-zA-Z]+".toRegex().matches(input) -> {
            val partsVar = input.replace("\\s".toRegex(), "").split("=")
            val sourceVar = partsVar[1]
            if (variablesStorage.containsKey(sourceVar)) {
                variablesStorage[partsVar[0]] = variablesStorage[sourceVar]!!
            } else {
                println("Unknown variable")
            }
        }
        // Match a variable name retrieval
        "[a-zA-Z]+".toRegex().matches(input) -> {
            if (variablesStorage.containsKey(input)) {
                println(variablesStorage[input])
            } else {
                println("Unknown variable")
            }
        }

        "[a-zA-Z]+\\s*=\\s*".toRegex().containsMatchIn(input) -> {
            println("Invalid assignment")
        }
        // If none of the patterns match, you can optionally handle it
        else -> {
            println("Invalid identifier")
        }
    }
}