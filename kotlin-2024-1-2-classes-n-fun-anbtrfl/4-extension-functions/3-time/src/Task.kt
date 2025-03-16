val Int.milliseconds: Time
    get() = Time((this / 1000).toLong(), this % 1000)

val Int.seconds: Time
    get() = Time(this.toLong(), 0)

val Int.minutes: Time
    get() = Time(this.toLong() * 60, 0)

val Int.hours: Time
    get() = Time(this.toLong() * 3600, 0)

operator fun Time.plus(other: Time): Time =
    Time(
        this.seconds + other.seconds + (this.milliseconds + other.milliseconds) / 1000,
        (this.milliseconds + other.milliseconds) % 1000,
    )

operator fun Time.minus(other: Time): Time =
    Time(
        (this.seconds * 1000 + this.milliseconds - (other.seconds * 1000 + other.milliseconds)) / 1000,
        ((this.seconds * 1000 + this.milliseconds - (other.seconds * 1000 + other.milliseconds)) % 1000).toInt(),
    )

operator fun Time.times(times: Int): Time =
    Time(
        (this.seconds * 1000 + this.milliseconds) * times / 1000,
        ((this.seconds * 1000 + this.milliseconds) * times % 1000).toInt(),
    )
