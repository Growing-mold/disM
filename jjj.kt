fun main() {
    val expr = "a|| !c || ( b &&!c) || !d&&e <-> a && b -> c"
    val list = parseExpr(expr)
    println("expr = $list")
    val operators = setOf("!", "&&", "||", "->", "<->")
    val operands = setOf("a", "b", "c", "d", "e")
    val stack = mutableListOf<String>()
    val result = mutableListOf<String>()
    for ((i,s) in list.withIndex()) {
        when (s) {
            in operands -> {
                result.add(s)
            }
            "(" -> {
                stack.push(s)
            }
            ")" -> {
                while (stack.isNotEmpty() && stack.peek() != "(") {
                    val x = stack.pop()
                    result.add(x)
                }
                assert(stack.peek() == "(")
                stack.pop()
            }
            else -> {
                assert(s in operators)
                handleOp(s, stack, result)
            }
        }
//        println("$stack: $result  +  ${list.getOrNull(i + 1)}")
    }
    while (stack.isNotEmpty()) {
        val topOp = stack.pop()
        result.add(topOp)

    }
    println("$result")

    val list1 = listOf(false, true)
    var dnf = ""
    var knf = ""
    for (a in list1) for (b in list1) for (c in list1) for (d in list1) for (e in list1) {
        val result1 = calcPostfix(result, a, b, c, e, d)
        println("${a.toInt()} ${b.toInt()} ${c.toInt()} ${d.toInt()} ${e.toInt()} -> ${calcPostfix(result, a, b, c, e, d)}")
        if (result1) {
            val x = "${if (a) "a" else "!a"} && ${if (b) "b" else "!b"} && ${if (c) "c" else "!c"}" +
                    " && ${if (d) "d" else "!d"} && ${if (e) "e" else "!e"} "
            dnf += "|| $x"
        }
        if (!result1) {
            val x = "${if (a) "!a" else "a"} || ${if (b) "!b" else "b"} || ${if (c) "!c" else "c"}" +
                    " || ${if (d) "!d" else "d"} || ${if (e) "!e" else "e"}"
            knf += "&& ($x) "
        }
    }
    println("ДНФ: ${dnf.removeRange(0..2)}")
    println("КНФ: ${knf.removeRange(0..2)}")
}

fun String.precedence(): Int = when (this) {
    "!" -> 5
    "&&" -> 4
    "||" -> 3
    "->" -> 2
    "<->" -> 1
    else -> throw NoSuchElementException()
}

fun handleOp(op: String, stack: MutableList<String>, result: MutableList<String>) {
    val opPrec = op.precedence()
    var topOp = stack.peek()
    if (topOp == null || topOp == "(") {
        stack.push(op)
        return
    }
    if (opPrec > topOp.precedence()) {
        stack.push(op)
        return
    }
    while ((topOp != null && topOp != "(") && topOp.precedence() >= opPrec) {
        stack.pop()
        result.add(topOp)
        topOp = stack.peek()
    }
    stack.push(op)
}

fun <T> MutableList<T>.push(x: T) = add(x)

fun <T> MutableList<T>.pop(): T = removeLast()

fun <T> MutableList<T>.peek(): T? = lastOrNull()

fun parseExpr(expr: String): List<String> {
    val pattern = "a|b|c|d|e|&&|\\|\\||\\(|\\)|!|->|<->".toRegex()
    val list = pattern.findAll(expr).map { it.value }.toList()
    return list
}





fun calcPostfix(
    postfix: List<String>, a: Boolean, b: Boolean,
    c: Boolean, d: Boolean, e: Boolean
): Boolean {
    val stack = mutableListOf<Boolean>()
    for (s in postfix) {
        when (s) {
            "a" -> {
                stack.add(a)
            }
            "b" -> {
                stack.add(b)
            }
            "c" -> {
                stack.add(c)
            }
            "d" -> {
                stack.add(d)
            }
            "e" -> {
                stack.add(e)
            }
            "&&" -> {
                val y = stack.removeLast()
                val x = stack.removeLast()
                stack.add(x && y)
            }
            "||" -> {
                val y = stack.removeLast()
                val x = stack.removeLast()
                stack.add(x || y)
            }
            "->" -> {
                val y = stack.removeLast()
                val x = stack.removeLast()
                stack.add(!x || y)
            }
            "<->" -> {
                val y = stack.removeLast()
                val x = stack.removeLast()
                stack.add((!x || y) && (!y || x))
            }
            "!" -> {
                val x = stack.removeLast()
                stack.add(!x)
            }
        }
        //       println(stack)
    }
    val result = stack.last()
//    println("stack size = ${stack.size}")
    return result
}


fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

