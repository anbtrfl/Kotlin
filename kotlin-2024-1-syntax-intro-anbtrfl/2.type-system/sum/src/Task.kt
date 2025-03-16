fun sum(a: Any, b: Any): Any? =
    when {
        a is String && b is String -> a + b
        a is Int && b is Int -> a + b
        a is Long && b is Long -> a + b
        a is Boolean && b is Boolean -> a or b
        else -> null
    }
