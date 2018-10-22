package com.myco.baskets

import org.springframework.util.Assert


data class Thing(
        val name: String,
        val description: String
) {

  fun validate() {
    assertRequiredString(name, "name");
    assertRequiredString(description, "description")
  }
}
