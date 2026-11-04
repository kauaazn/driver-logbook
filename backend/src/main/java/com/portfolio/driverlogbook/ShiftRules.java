package com.portfolio.driverlogbook;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Business rules shared by the shift endpoints and their unit tests. */
@Component
class ShiftRules {
    private final ShiftRepository shifts;

    ShiftRules(ShiftRepository shifts) {
        this.shifts = shifts;
    }

    void ensureNoActiveShift() {
        if (shifts.findFirstByEndedAtIsNull().isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "There is already an active shift. End it before starting another.");
        }
    }

    void ensureEndingMileageIsValid(int startKm, int endKm) {
        if (endKm < startKm) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Ending mileage cannot be lower than starting mileage.");
        }
    }
}
