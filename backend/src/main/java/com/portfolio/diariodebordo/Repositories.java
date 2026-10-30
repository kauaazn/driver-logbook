package com.portfolio.diariodebordo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

interface VehicleRepository extends JpaRepository<Vehicle,Long> { boolean existsByFleetNumberIgnoreCase(String fleetNumber); }
interface ShiftRepository extends JpaRepository<Shift,Long> { Optional<Shift> findFirstByEndedAtIsNull(); List<Shift> findAllByOrderByStartedAtDesc(); List<Shift> findByVehicleIdOrderByStartedAtDesc(Long vehicleId); }
interface InspectionRepository extends JpaRepository<Inspection,Long> {}
interface DamageRepository extends JpaRepository<Damage,Long> { List<Damage> findAllByOrderByCreatedAtDesc(); }
