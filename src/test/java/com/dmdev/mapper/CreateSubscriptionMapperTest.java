package com.dmdev.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

class CreateSubscriptionMapperTest {

  private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

  @Test
  void testMapMethod_WhenDtoIsPassed_ShouldReturnSubscription() {
    // given
    var dto = CreateSubscriptionDto.builder()
        .userId(1)
        .name("name")
        .provider("GOOGLE")
        .build();

    // when
    var actualResult = mapper.map(dto);
    Subscription subscription = Subscription.builder()
        .userId(1)
        .name("name")
        .provider(Provider.GOOGLE)
        .status(Status.ACTIVE)
        .build();

    // then
    assertThat(actualResult).isEqualTo(subscription);
  }
}