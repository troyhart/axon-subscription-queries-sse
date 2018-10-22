package com.myco.baskets

import org.springframework.util.Assert


fun assertRequiredString(value: String, name: String) {
  Assert.hasText(value, name + " is required");
}
