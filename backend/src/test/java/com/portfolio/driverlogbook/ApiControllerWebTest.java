package com.portfolio.driverlogbook;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
}
