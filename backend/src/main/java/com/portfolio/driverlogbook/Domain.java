package com.portfolio.driverlogbook;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="vehicles", uniqueConstraints=@UniqueConstraint(columnNames={"fleet_number"}))
class Vehicle {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(name="fleet_number", nullable=false) String fleetNumber;
    @Column(nullable=false) String model;
    @Column(nullable=false, unique=true) String plate;
    Instant createdAt=Instant.now();
    protected Vehicle() {}
    Vehicle(String fleetNumber,String model,String plate){this.fleetNumber=fleetNumber;this.model=model;this.plate=plate;}
}

@Entity @Table(name="shifts")
class Shift {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) Vehicle vehicle;
    @Column(nullable=false) Instant startedAt;
    Instant endedAt;
    @Column(nullable=false) Integer startKm;
    Integer endKm;
    String note;
    Instant createdAt=Instant.now();
    Instant updatedAt=Instant.now();
    protected Shift() {}
    Shift(Vehicle vehicle,Integer startKm){this.vehicle=vehicle;this.startKm=startKm;this.startedAt=Instant.now();}
}

@Entity @Table(name="inspections")
class Inspection {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) Shift shift;
    @Column(nullable=false) String phase;
    @Column(nullable=false) String item;
    @Column(nullable=false) boolean ok;
    String notes;
    Instant recordedAt=Instant.now();
    protected Inspection() {}
    Inspection(Shift shift,String phase,String item,boolean ok,String notes){this.shift=shift;this.phase=phase;this.item=item;this.ok=ok;this.notes=notes;}
}

@Entity @Table(name="damages")
class Damage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) Vehicle vehicle;
    @Column(nullable=false,length=1000) String description;
    @Enumerated(EnumType.STRING) @Column(nullable=false) DamageStatus status=DamageStatus.OPEN;
    @ManyToOne(fetch=FetchType.LAZY) Shift shift;
    Instant createdAt=Instant.now();
    Instant updatedAt=Instant.now();
    protected Damage() {}
    Damage(Vehicle vehicle,String description,Shift shift){this.vehicle=vehicle;this.description=description;this.shift=shift;}
}
enum DamageStatus { OPEN, IN_MAINTENANCE, RESOLVED }
