package com.dmdev.integration.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SubscriptionDaoIT extends IntegrationTestBase {

  SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

  @Test
  void testFindAll_WhenAddedSubscriptionsWithDiffNames_ShouldReturnAll() {
    Subscription subscription1 = subscriptionDao.upsert(getSubscription(2));
    Subscription subscription2 = subscriptionDao.upsert(getSubscription(1));
    Subscription subscription3 = subscriptionDao.upsert(getSubscription(3));

    var actualResult = subscriptionDao.findAll();

    assertThat(actualResult).hasSize(3);

    List<Integer> ids = actualResult.stream().map(Subscription::getId).toList();

    assertThat(ids).containsExactlyInAnyOrder(subscription1.getId(), subscription2.getId(),
        subscription3.getId());

  }

  @Test
  void testFindById_WhenSubscriptionAdded_ShouldReturnSubscription() {
    Subscription subscription = subscriptionDao.upsert(getSubscription(1));

    Optional<Subscription> actualSubscription = subscriptionDao.findById(subscription.getId());

    assertThat(actualSubscription).isPresent();

    assertThat(actualSubscription.get()).isEqualTo(subscription);
  }

  @Test
  void testSubscriptionDelete_WhenDeleteExisted_shouldReturnTrue() {
    Subscription subscription = subscriptionDao.upsert(getSubscription(1));

    boolean actualResult = subscriptionDao.delete(subscription.getId());

    assertTrue(actualResult);
  }

  @Test
  void testSubscriptionDelete_WhenDeleteUnexisted_shouldReturnFalse() {
    Subscription subscription = subscriptionDao.upsert(getSubscription(1));

    boolean actualResult = subscriptionDao.delete(123);

    assertFalse(actualResult);
  }

  @Test
  void testUpdate_WhenSubscriptionExists_ShouldUpdateSuccessfully() {
    Subscription subscription = subscriptionDao.upsert(getSubscription(1));

    subscription.setProvider(Provider.APPLE);
    subscription.setStatus(Status.CANCELED);
    subscriptionDao.update(subscription);

    Subscription actualSubscription = subscriptionDao.findById(subscription.getId()).orElseThrow();

    assertThat(actualSubscription).isEqualTo(subscription);

  }

  @Test
  void testInsertSubscription_WhenSubscriptionExists_ShouldFindByUserId() {
    Subscription subscription = getSubscription(1);

    subscriptionDao.insert(subscription);

    assertNotNull(subscription.getId());
    assertEquals(subscription, subscriptionDao.findById(subscription.getId()).orElseThrow());
  }

  @Test
  void testFindByUserId_WhenSubscriptionExists_ShouldFindByUserId() {
    Subscription subscription = subscriptionDao.upsert(getSubscription(3));

    Optional<Subscription> actualSubscription = subscriptionDao.findByUserId(subscription.getUserId()).stream()
        .toList().stream().findFirst();

    assertThat(actualSubscription).isPresent();

    assertThat(actualSubscription.get()).isEqualTo(subscription);
  }

  private Subscription getSubscription(Integer userId) {
    return Subscription.builder()
        .userId(userId)
        .name("name")
        .provider(Provider.GOOGLE)
        .expirationDate(Instant.parse("2023-09-25T10:15:30Z"))
        .status(Status.ACTIVE)
        .build();
  }
}