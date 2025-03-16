class BankAccount(amount: Int) {
    var balance: Int = 0
        private set(value) {
            logTransaction(field, value)
            field = value
        }

    init {
        require(amount >= 0) { "amount cannot be negative" }
        this.balance = amount
    }

    fun deposit(amount: Int) {
        check(amount)
        this.balance += amount
    }

    fun withdraw(amount: Int) {
        check(amount)
        require(balance - amount >= 0) { "impossible to withdraw amount greater than current balance" }
        this.balance -= amount
    }

    private fun check(amount: Int) {
        require(amount > 0) { "please, amount must be > 0" }
    }
}

fun logTransaction(from: Int, to: Int) {
    println("$from -> $to")
}
