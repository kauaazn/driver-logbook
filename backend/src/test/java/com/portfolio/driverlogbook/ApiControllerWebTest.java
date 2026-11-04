package com.portfolio.driverlogbook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerWebTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleRepository vehicles;

    @MockitoBean
    private ShiftRepository shifts;

    @MockitoBean
    private InspectionRepository inspections;

    @MockitoBean
    private DamageRepository damages;

    @MockitoBean
    private ShiftRules shiftRules;

    @Test
    void listsVehiclesAndReturnsOk() throws Exception {
        // Arrange: the mocked repository has no vehicles to return.
        when(vehicles.findAll()).thenReturn(List.of());

        // Act + assert: exercise the real MVC route and check its HTTP response.
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void listsShiftsAndReturnsOk() throws Exception {
        when(shifts.findAllByOrderByStartedAtDesc()).thenReturn(List.of());

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void rejectsVehicleWithMissingRequiredField() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fleetNumber":"F-204","model":"City Bus"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createsVehicleAndReturnsCreated() throws Exception {
        when(vehicles.existsByFleetNumberIgnoreCase("F-204")).thenReturn(false);
        when(vehicles.save(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle vehicle = invocation.getArgument(0);
            vehicle.id = 204L;
            return vehicle;
        });

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fleetNumber":"F-204","model":"City Bus","plate":"abc1d23"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(204))
                .andExpect(jsonPath("$.plate").value("ABC1D23"));
    }

    @Test
    void rejectsShiftWithMissingStartingMileage() throws Exception {
        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vehicleId":204,"inspection":[]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void startsShiftAndReturnsCreated() throws Exception {
        Vehicle vehicle = new Vehicle("F-204", "City Bus", "ABC1D23");
        vehicle.id = 204L;
        when(vehicles.findById(204L)).thenReturn(Optional.of(vehicle));
        when(shifts.save(any(Shift.class))).thenAnswer(invocation -> {
            Shift shift = invocation.getArgument(0);
            shift.id = 1L;
            return shift;
        });

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vehicleId":204,"startKm":120000,"inspection":[]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vehicleId").value(204))
                .andExpect(jsonPath("$.startKm").value(120000));
    }

    @Test
    void returnsNotFoundWhenStartingShiftForUnknownVehicle() throws Exception {
        when(vehicles.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"vehicleId":999,"startKm":120000,"inspection":[]}
                                """))
                .andExpect(status().isNotFound());
    }
}
