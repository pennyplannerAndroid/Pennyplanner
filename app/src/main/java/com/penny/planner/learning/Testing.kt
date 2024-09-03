package com.penny.planner.learning

// Factory pattern

fun main() {
    println(Pizza.create("Chicken"))
    // Pizza() can't be called as Pizza has private constructor so everyobe has to call create() to make pizza object
}
class Testing {

}

class Pizza private constructor(val type: String, val toppings: String) {
    companion object Factory {
        fun create(pizzaType: String) : Pizza {
            return when(pizzaType) {
                "Chicken" -> Pizza("Chicken", "Onion, Chicken, Capsicum")
                "Paneer" -> Pizza(type = "Paneer", toppings = "Paneer, Onion")
                else -> Pizza("Margherita", "Onions")
            }
        }
    }

    override fun toString(): String {
        return this.type + this.toppings
    }
}