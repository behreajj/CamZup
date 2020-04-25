import camzup.core.Vec2;
import camzup.core.Utils;

class KVec2 : Vec2 {
    constructor() : super()
    constructor(x: Boolean = false, y: Boolean = false) : super(x, y)
    constructor(x: Int = 0, y: Int = 0) : super(x, y)
    constructor(x: Float = 0.0f, y: Float = 0.0f) : super(x, y)
    constructor(x: String = "0.0", y: String = "0.0") : super(x, y)
    constructor(v: Vec2 = Vec2()) : super(v)

    operator fun dec(): KVec2 {
        return KVec2(this.x - 1.0f, this.y - 1.0f)
    }

    operator fun divAssign(b: Int) {
        if (b != 0) {
            this.x = this.x / b
            this.y = this.y / b
        } else {
            this.x = 0.0f
            this.y = 0.0f
        }
    }

    operator fun divAssign(b: Float) {
        if (b != 0.0f) {
            this.x = this.x / b
            this.y = this.y / b
        } else {
            this.x = 0.0f
            this.y = 0.0f
        }
    }

    operator fun divAssign(b: KVec2) {
        this.x = Utils.div(this.x, b.x)
        this.y = Utils.div(this.y, b.y)
    }

    operator fun inc(): KVec2 {
        return KVec2(this.x + 1.0f, this.y + 1.0f)
    }

    operator fun minusAssign(b: KVec2) {
        this.x = this.x - b.x
        this.y = this.y - b.y
    }

    operator fun not() {
        if (this.x != 0.0f) {
            this.x = 0.0f;
        } else {
            this.x = 1.0f;
        }

        if (this.y != 0.0f) {
            this.y = 0.0f;
        } else {
            this.y = 1.0f;
        }
    }

    operator fun plusAssign(b: KVec2) {
        this.x = this.x + b.x
        this.y = this.y + b.y
    }

    operator fun remAssign(b: Int) {
        if (b != 0) {
            this.x = this.x % b;
            this.y = this.y % b;
        }
    }

    operator fun remAssign(b: Float) {
        if (b != 0.0f) {
            this.x = this.x % b;
            this.y = this.y % b;
        }
    }

    operator fun remAssign(b: KVec2) {
        this.x = Utils.fmod(this.x, b.x)
        this.y = Utils.fmod(this.y, b.y)
    }

    operator fun timesAssign(b: Int) {
        this.x = this.x * b
        this.y = this.y * b
    }

    operator fun timesAssign(b: Float) {
        this.x = this.x * b
        this.y = this.y * b
    }

    operator fun timesAssign(b: KVec2) {
        this.x = this.x * b.x
        this.y = this.y * b.y
    }

    operator fun unaryMinus() {
        this.x = -this.x
        this.y = -this.y
    }

    operator fun unaryPlus() {
        this.x = +this.x
        this.y = +this.y
    }
}