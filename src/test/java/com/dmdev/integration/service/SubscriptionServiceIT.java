package com.dmdev.integration.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.service.SubscriptionService;
import com.dmdev.validator.CreateSubscriptionValidator;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubscriptionServiceIT extends IntegrationTestBase {

  SubscriptionService subscriptionService;

  SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

  @BeforeEach
  void init() {
    subscriptionService = new SubscriptionService(
        subscriptionDao,
        CreateSubscriptionMapper.getInstance(),
        CreateSubscriptionValidator.getInstance(),
        Clock.fixed(Instant.now(), ZoneId.systemDefault())
        );
  }

  @Test
  void testUpsert_WhenUpsertSuccessfully_ShouldReturnUser() {
    CreateSubscriptionDto dto = getSubscriptionDto();
    Subscription subscription = subscriptionService.upsert(dto);

    assertThat(subscription.getId()).isNotNull();
    assertThat(subscription.getName()).isEqualTo(dto.getName());
  }

  private CreateSubscriptionDto getSubscriptionDto() {
    return CreateSubscriptionDto.builder()
        .userId(1)
        .name("name")
        .provider("GOOGLE")
        .expirationDate(Instant.parse("2023-09-25T10:15:30Z"))
        .build();
  }


}
