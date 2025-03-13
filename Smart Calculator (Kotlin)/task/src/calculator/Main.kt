package calculator

import java.util.*

fun main() {
    val variablesStorage = mutableMapOf<String, Int>()
    while (true) {
        val input = readln().trim()
        val regexVar = "^[a-zA-Z]+\\s*=\\s*-?\\d+$".toRegex() // For variable assignment with an integer
        val regexVarAssignWithVar = "^[a-zA-Z]+\\s*=\\s*[a-zA-Z]+$".toRegex() // For variable assignment with another variable
        val regexRetrieveVar = "^[a-zA-Z]+$".toRegex() // For variable retrieval
        val regexExp = "([\\d]+|[a-zA-Z]+)(\\s*[-+*/]+\\s*([\\d]+|[a-zA-Z]+))*".toRegex()
        val regCmd = "^/".toRegex()
        val regexVarWithNums = "^[a-zA-Z]+\\d+".toRegex()
        val regexVarAssignWithShit = "^[a-zA-Z]+\\s*=\\s*".toRegex()
        when {
            input == "/exit" -> {
                println("Bye!")
                break
            }
            input == "/help" -> {
                println("The program supports multiplication, division, sum, subtraction of math expressions.")
                continue
            }
            regCmd.containsMatchIn(input) -> {
                println("Unknown command")
                continue
            }
            input.isEmpty() -> continue
            regexVar.matches(input) -> {
                processVar(input, variablesStorage)
                continue // Continue to the next iteration
            }
            regexVarAssignWithVar.matches(input) -> {
                processVar(input, variablesStorage)
                continue // Continue to the next iteration
            }
            regexRetrieveVar.matches(input) -> {
                processVar(input, variablesStorage)
                continue // Continue to the next iteration
            }
            regexVarWithNums.containsMatchIn(input) -> {
                processVar(input, variablesStorage)
                continue
            }
            regexVarAssignWithShit.containsMatchIn(input) -> {
                processVar(input, variablesStorage)
                continue
            }
            regexExp.containsMatchIn(input) -> {
                processExpressionToPostfix(input, variablesStorage)
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

fun processExpressionToPostfix(input: String, variablesStorage: MutableMap<String, Int>) {
    if (input.count{it == '('} != input.count {it == ')'} || "[*/]{2,}".toRegex().containsMatchIn(input)) {
        println("Invalid expression")
        return
    }
    // Normalize the input expression
    val normalizedExp = input.replace("\\s+".toRegex(), " ").trim()
    val normalizedExpression = normalizeOperators(normalizedExp)

    // Split the expression into components while keeping operators
    val regex = "(\\+|-|\\d*\\.?\\d+|[a-zA-Z_][a-zA-Z0-9_]*|[*/()+-])".toRegex()
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

    val postfixTokens = mutableListOf<String>()
    val tokenStack: Stack<String> = Stack()

    // Define operator precedence
    val precedence = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2
    )

    for (token in resolvedTokens) {
        when {
            token.matches(Regex("-?\\d+")) -> postfixTokens.add(token)  // Numbers go directly to the output
            token in precedence.keys -> { // Handle operators
                while (tokenStack.isNotEmpty() && (precedence[tokenStack.peek()] ?: 0) >= precedence[token]!!) {
                    postfixTokens.add(tokenStack.pop())
                }
                tokenStack.push(token)
            }
            token == "(" -> tokenStack.push(token) // Push '(' onto the stack
            token == ")" -> { // Process until matching '('
                while (tokenStack.isNotEmpty() && tokenStack.peek() != "(") {
                    postfixTokens.add(tokenStack.pop())
                }
                tokenStack.pop() // Pop the '(' from the stack
            }
        }
    }

    // Pop all the remaining operators in the stack
    while (tokenStack.isNotEmpty()) {
        postfixTokens.add(tokenStack.pop())
    }

    val postfixExpression = postfixTokens.joinToString(" ")
//    println(postfixExpression)
    println(countPostfixExpression(postfixExpression))
}

fun countPostfixExpression(postfixExpression: String): String {
    // Split the expression into components while keeping operators
    val regex = "([-+]?\\d+|[a-zA-Z]+|[*/()+-])".toRegex()
    val tokens = regex.findAll(postfixExpression).map { it.value }.toList()
    var result: Int
    val postfixStack: Stack<String> = Stack()
    for (token in tokens) {
        when {
            token.matches(Regex("-?\\d+")) -> postfixStack.push(token)
            token.matches(Regex("[*/+-]")) -> {
                val num2 = postfixStack.pop().toInt()
                val num1 = postfixStack.pop().toInt()
                result = when (token) {
                    "*" -> num1 * num2
                    "/" -> num1 / num2
                    "+" -> num1 + num2
                    "-" -> num1 - num2
                    else -> 0
                }
                postfixStack.push(result.toString())
            }
        }
    }
    return postfixStack.pop()
}

fun processVar(input: String, variablesStorage: MutableMap<String, Int>) {
    when {
        "[a-zA-Z]+\\s*=\\s*-?\\d+".toRegex().matches(input) -> {
            val partsVar = input.split("=").map { it.trim() }
            if (partsVar.size == 2) {
                val variableName = partsVar[0]
                val valueString = partsVar[1]

                // Check if variable name contains only letters
                if (variableName.all { it.isLetter() }) {
                    try {
                        val value = valueString.toInt()
                        variablesStorage[variableName] = value
                    } catch (e: NumberFormatException) {
                        println("Invalid assignment")
                    }
                } else {
                    println("Invalid variable name")
                }
            } else {
                println("Invalid assignment")
            }
        }
        "[a-zA-Z]+\\s*=\\s*[a-zA-Z]+".toRegex().matches(input) -> {
            val partsVar = input.split("=").map { it.trim() }
            if (partsVar.size == 2) {
                val sourceVar = partsVar[1]
                // Check if variable names contain only letters
                if (partsVar[0].all { it.isLetter() } && sourceVar.all { it.isLetter() }) {
                    if (variablesStorage.containsKey(sourceVar)) {
                        variablesStorage[partsVar[0]] = variablesStorage[sourceVar]!!
                    } else {
                        println("Unknown variable")
                    }
                } else {
                    println("Invalid variable name")
                }
            } else {
                println("Invalid assignment")
            }
        }

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
        else -> {
            println("Invalid identifier")
        }
    }
}