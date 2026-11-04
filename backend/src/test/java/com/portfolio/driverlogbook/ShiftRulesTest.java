package com.portfolio.driverlogbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftRulesTest {
    @Mock
    private ShiftRepository shifts;

    private ShiftRules rules;

    @BeforeEach
    void setUp() {
        rules = new ShiftRules(shifts);
    }

    @Test
    void rejectsStartingAnotherShiftWhenOneIsAlreadyActive() {
        // Arrange: make the repository report an unfinished shift.
        when(shifts.findFirstByEndedAtIsNull()).thenReturn(Optional.of(mock(Shift.class)));

        // Act + assert: the rule must stop the operation with HTTP 409 Conflict.
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                rules::ensureNoActiveShift);

        assertThat(exception.getStatusCode().value()).isEqualTo(409);
        verify(shifts).findFirstByEndedAtIsNull();
    }

    @Test
    void acceptsStartingAShiftWhenThereIsNoActiveShift() {
        when(shifts.findFirstByEndedAtIsNull()).thenReturn(Optional.empty());

        assertDoesNotThrow(rules::ensureNoActiveShift);
        verify(shifts).findFirstByEndedAtIsNull();
    }

    @Test
    void rejectsEndingMileageBelowStartingMileage() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rules.ensureEndingMileageIsValid(120_000, 119_999));

        assertThat(exception.getStatusCode().value()).isEqualTo(422);
    }

    @Test
    void acceptsEndingMileageEqualToStartingMileage() {
        assertDoesNotThrow(() -> rules.ensureEndingMileageIsValid(120_000, 120_000));
    }
}
