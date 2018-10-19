package com.myco.baskets

import org.springframework.util.Assert

data class Thing(
        val name: String,
        val description: String
) {
  fun validate() {
    validateRequiredString(name, "name");
    validateRequiredString(description, "description")
  }
}