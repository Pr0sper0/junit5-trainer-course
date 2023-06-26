package com.dmdev.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PropertiesUtilTest {

  @ParameterizedTest
  @MethodSource("getPropertyArguments")
  void testGet_WhenPropertyExists_ShouldReturnProperty(String key, String expected) {
    var actual = PropertiesUtil.get(key);
    assertThat(actual).isEqualTo(expected);
  }

  static Stream<Arguments> getPropertyArguments() {
    return Stream.of(
        Arguments.of("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
        Arguments.of("db.user", "sa"),
        Arguments.of("db.password", "pass")
    );
  }
}