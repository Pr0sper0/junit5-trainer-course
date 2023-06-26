package com.dmdev.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dmdev.dto.CreateSubscriptionDto;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CreateSubscriptionValidatorTest {

  private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

  @Test
  void testValidate_WhenValid_ShouldReturnEmptyValidationResult() {
    var dto = CreateSubscriptionDto.builder()
        .userId(1)
        .name("name")
        .provider("GOOGLE")
        .expirationDate(Instant.parse("2023-09-25T10:15:30Z"))
        .build();

    ValidationResult validationResult = validator.validate(dto);

    assertTrue(validationResult.getErrors().isEmpty());
  }

  @ParameterizedTest
  @MethodSource("getValidationErrorArguments")
  void testValidate_WhenOneFieldIsInvalid_ShouldReturnValidationResultWithCorrectError(
      Integer id, String name, String provider, Instant date, String expectedErrorCode) {
    var dto = CreateSubscriptionDto.builder()
        .userId(id)
        .name(name)
        .provider(provider)
        .expirationDate(date)
        .build();

    ValidationResult validationResult = validator.validate(dto);

    assertFalse(validationResult.getErrors().isEmpty());
    assertThat(validationResult.getErrors()).hasSize(1);
    assertThat(validationResult.getErrors().get(0).getCode().toString()).isEqualTo(
        expectedErrorCode);
  }

  @Test
  void testValidate_WhenExpirationDateIsBeforeNow_ShouldReturnValidationResultWithError() {
    var dto = CreateSubscriptionDto.builder()
        .userId(1)
        .name("name")
        .provider("GOOGLE")
        .expirationDate(Instant.parse("2020-09-25T10:15:30Z"))
        .build();

    ValidationResult validationResult = validator.validate(dto);

    assertFalse(validationResult.getErrors().isEmpty());
    assertThat(validationResult.getErrors()).hasSize(1);
    assertThat(validationResult.getErrors().get(0).getCode().toString()).isEqualTo("103");
  }


  @Test
  void testValidate_WhenAllFieldsAreInvalid_ShouldReturnValidationResultWithErrors() {
    var dto = CreateSubscriptionDto.builder()
        .userId(null)
        .name("")
        .provider("")
        .expirationDate(null)
        .build();

    ValidationResult validationResult = validator.validate(dto);

    assertFalse(validationResult.getErrors().isEmpty());
    assertThat(validationResult.getErrors()).hasSize(4);

    List<String> errorCodes = validationResult.getErrors().stream()
        .map(error -> error.getCode().toString()).toList();

    assertThat(errorCodes).contains("100", "101", "102", "103");
  }

  /**
   * @return arguments:
   */
  private static Stream<Arguments> getValidationErrorArguments() {
    return Stream.of(
        Arguments.of(null, "name", "GOOGLE", Instant.parse("2023-09-25T10:15:30Z"), "100"),
        Arguments.of(1, "", "GOOGLE", Instant.parse("2023-09-25T10:15:30Z"), "101"),
        Arguments.of(1, "name", "", Instant.parse("2023-09-25T10:15:30Z"), "102"),
        Arguments.of(1, "name", "GOOGLE", null, "103")
    );
  }

}