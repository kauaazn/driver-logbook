package com.portfolio.diariodebordo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;

@RestController @RequestMapping("/api")
class ApiController {
    private final VehicleRepository vehicles; private final ShiftRepository shifts; private final InspectionRepository inspections; private final DamageRepository damages;
    ApiController(VehicleRepository vehicles,ShiftRepository shifts,InspectionRepository inspections,DamageRepository damages){this.vehicles=vehicles;this.shifts=shifts;this.inspections=inspections;this.damages=damages;}
    record VehicleRequest(@NotBlank String fleetNumber,@NotBlank String model,@NotBlank String plate) {}
    record CheckRequest(@NotBlank String item,boolean ok,String notes) {}
    record StartRequest(@NotNull Long vehicleId,@NotNull @PositiveOrZero Integer startKm,List<CheckRequest> inspection) {}
    record FinishRequest(@NotNull @PositiveOrZero Integer endKm,String note,List<CheckRequest> inspection) {}
    record DamageRequest(@NotNull Long vehicleId,@NotBlank @Size(max=1000) String description) {}
    record VehicleView(Long id,String fleetNumber,String model,String plate) {}
    record ShiftView(Long id,Long vehicleId,Instant startedAt,Instant endedAt,Integer startKm,Integer endKm,String note) {}
    record DamageView(Long id,String description,Long vehicleId,String status,Instant createdAt,Instant updatedAt,Long shiftId) {}
    record Dashboard(List<VehicleView> vehicles,List<ShiftView> shifts,List<DamageView> damages) {}
    @GetMapping("/health") Map<String,String> health(){return Map.of("status","ok");}
    @GetMapping("/dashboard") Dashboard dashboard(){return new Dashboard(vehicles.findAll().stream().map(this::view).toList(),shifts.findAllByOrderByStartedAtDesc().stream().map(this::view).toList(),damages.findAllByOrderByCreatedAtDesc().stream().map(this::view).toList());}
    @GetMapping("/vehicles") List<VehicleView> listVehicles(){return vehicles.findAll().stream().map(this::view).toList();}
    @PostMapping("/vehicles") ResponseEntity<VehicleView> createVehicle(@Valid @RequestBody VehicleRequest r){if(vehicles.existsByFleetNumberIgnoreCase(r.fleetNumber()))throw conflict("Número de frota já cadastrado.");Vehicle v=vehicles.save(new Vehicle(r.fleetNumber().trim(),r.model().trim(),r.plate().trim().toUpperCase()));return ResponseEntity.status(HttpStatus.CREATED).body(view(v));}
    @GetMapping("/shifts") List<ShiftView> listShifts(@RequestParam(required=false) Long vehicleId){return (vehicleId==null?shifts.findAllByOrderByStartedAtDesc():shifts.findByVehicleIdOrderByStartedAtDesc(vehicleId)).stream().map(this::view).toList();}
    @PostMapping("/shifts") ResponseEntity<ShiftView> startShift(@Valid @RequestBody StartRequest r){if(shifts.findFirstByEndedAtIsNull().isPresent())throw conflict("Já existe um turno em andamento. Encerre-o antes de iniciar outro.");Vehicle v=vehicles.findById(r.vehicleId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Veículo não encontrado."));Shift s=shifts.save(new Shift(v,r.startKm()));saveInspection(s,"INICIAL",r.inspection());return ResponseEntity.status(HttpStatus.CREATED).body(view(s));}
    @PostMapping("/shifts/{id}/finish") ShiftView finishShift(@PathVariable Long id,@Valid @RequestBody FinishRequest r){Shift s=shifts.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Turno não encontrado."));if(s.endedAt!=null)throw conflict("Este turno já foi encerrado.");if(r.endKm()<s.startKm)throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"A quilometragem final não pode ser menor que a inicial.");s.endKm=r.endKm();s.note=r.note();s.endedAt=Instant.now();s.updatedAt=Instant.now();s=shifts.save(s);saveInspection(s,"FINAL",r.inspection());return view(s);}
    @PostMapping("/damages") ResponseEntity<DamageView> createDamage(@Valid @RequestBody DamageRequest r){Vehicle v=vehicles.findById(r.vehicleId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Veículo não encontrado."));Shift active=shifts.findFirstByEndedAtIsNull().orElse(null);Damage d=damages.save(new Damage(v,r.description().trim(),active));return ResponseEntity.status(HttpStatus.CREATED).body(view(d));}
    @PatchMapping("/damages/{id}/status") DamageView updateDamage(@PathVariable Long id,@RequestParam DamageStatus status){Damage d=damages.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Avaria não encontrada."));d.status=status;d.updatedAt=Instant.now();return view(damages.save(d));}
    @GetMapping("/damages") List<DamageView> listDamages(@RequestParam(required=false) Long vehicleId,@RequestParam(required=false) DamageStatus status){return damages.findAllByOrderByCreatedAtDesc().stream().filter(d->vehicleId==null||d.vehicle.id.equals(vehicleId)).filter(d->status==null||d.status==status).map(this::view).toList();}
    private void saveInspection(Shift s,String phase,List<CheckRequest> entries){if(entries!=null)entries.forEach(c->inspections.save(new Inspection(s,phase,c.item(),c.ok(),c.notes())));}
    private VehicleView view(Vehicle v){return new VehicleView(v.id,v.fleetNumber,v.model,v.plate);}
    private ShiftView view(Shift s){return new ShiftView(s.id,s.vehicle.id,s.startedAt,s.endedAt,s.startKm,s.endKm,s.note);}
    private DamageView view(Damage d){return new DamageView(d.id,d.description,d.vehicle.id,d.status.name(),d.createdAt,d.updatedAt,d.shift==null?null:d.shift.id);}
    private ResponseStatusException conflict(String message){return new ResponseStatusException(HttpStatus.CONFLICT,message);}
}
