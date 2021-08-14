package com.painkillergis.fall_color_history.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.*

class ToJsonElementKtTest : FunSpec({
  test("empty object") {
    emptyMap<String, Any>().toJsonElement() shouldBe buildJsonObject { }
  }

  test("string key value") {
    mapOf("key" to "value").toJsonElement() shouldBe buildJsonObject {
      put("key", "value")
    }
  }

  test("string key numeric value") {
    mapOf("key" to 1234).toJsonElement() shouldBe buildJsonObject {
      put("key", 1234)
    }
  }

  test("string key boolean value") {
    mapOf("key" to false).toJsonElement() shouldBe buildJsonObject {
      put("key", false)
    }
  }

  test("nested object") {
    mapOf("key" to mapOf("nested key" to "value")).toJsonElement() shouldBe
      buildJsonObject {
        putJsonObject("key") {
          put("nested key", "value")
        }
      }
  }

  test("nested list") {
    mapOf("key" to listOf("value")).toJsonElement() shouldBe
      buildJsonObject {
        putJsonArray("key") {
          add("value")
        }
      }
  }

  test("null") {
    null.toJsonElement() shouldBe JsonNull
  }

  test("no effect on json object") {
    val jsonObject = buildJsonObject {
      put("key" , "string value")
      put("key2" , 1234)
      put("key3", false)
      putJsonArray("key4"){
        add("123")
        add(123)
        add(false)
        add(buildJsonArray {  })
        add(buildJsonObject {  })
      }
      putJsonObject("key5") {
        put("nested key", "value")
      }
    }

    jsonObject.toJsonElement() shouldBe jsonObject
  }
})
