package com.dmdev.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.AtLeast;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

  @Mock
  private CreateSubscriptionMapper createSubscriptionMapper;

  @Mock
  private CreateSubscriptionValidator createSubscriptionValidator;

  @Mock
  private SubscriptionDao subscriptionDao;

  @Mock
  private Clock clock;

  @InjectMocks
  private SubscriptionService subscriptionService;

  @Test
  void testSubscriptionUpsert_WhenUpsertSuccessfully_ShouldValidateInputAndConvertSavedEntity() {
    var subscriptionDto = CreateSubscriptionDto.builder().build();
    Subscription subscription = getSubscription();
    doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
    doReturn(subscription).when(createSubscriptionMapper).map(subscriptionDto);
    doReturn(subscription).when(subscriptionDao).upsert(subscription);

    Subscription actualResult = subscriptionService.upsert(subscriptionDto);

    assertSame(actualResult, subscription);
    verify(createSubscriptionValidator).validate(subscriptionDto);
    verify(createSubscriptionMapper).map(subscriptionDto);
    verify(subscriptionDao).upsert(subscription);
  }

  @Test
  void testSubscriptionUpsert_WhenValidationFailed_ShouldThrowValidationException() {
    var subscriptionDto = CreateSubscriptionDto.builder().build();
    ValidationResult validationResult = new ValidationResult();
    List<Error> errors = List.of(Error.of(101, "name is invalid"));
    validationResult.add(errors.get(0));
    doReturn(validationResult).when(createSubscriptionValidator).validate(subscriptionDto);

    var exception = assertThrows(ValidationException.class,
        () -> subscriptionService.upsert(subscriptionDto));

    assertThat(exception.getErrors()).containsExactlyInAnyOrderElementsOf(errors);
    verify(createSubscriptionValidator).validate(subscriptionDto);
    verify(createSubscriptionMapper, Mockito.never()).map(any());
    verify(subscriptionDao, Mockito.never()).upsert(any());
  }

  @Test
  void testSubscriptionCancel_WhenCancelSuccessfully_ShouldUpdateSubscriptionWithStatusCanceled() {
    var subscription = getSubscription();

    doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

    subscriptionService.cancel(subscription.getId());

    assertSame(Status.CANCELED, subscription.getStatus());
    verify(subscriptionDao).findById(subscription.getId());
  }

  @Test
  void testSubscriptionExpire_WhenExpired_ShouldUpdateSubscriptionWithStatusExpire() {

    var subscription = getSubscription();

    doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
    doReturn(Instant.now()).when(clock).instant();
    subscriptionService.expire(subscription.getId());

    assertSame(Status.EXPIRED, subscription.getStatus());
    verify(subscriptionDao).findById(subscription.getId());
    verify(clock, new AtLeast(1)).instant();
  }

  private Subscription getSubscription() {
    return Subscription.builder()
        .id(1)
        .userId(1)
        .name("name")
        .provider(Provider.GOOGLE)
        .expirationDate(Instant.parse("2023-09-25T10:15:30Z"))
        .status(Status.ACTIVE)
        .build();
  }
}