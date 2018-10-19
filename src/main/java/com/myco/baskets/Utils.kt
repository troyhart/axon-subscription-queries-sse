package com.myco.baskets

import org.springframework.util.Assert

fun validateRequiredString(value: String, name: String) {
  Assert.hasText(value, name + " is required");
}