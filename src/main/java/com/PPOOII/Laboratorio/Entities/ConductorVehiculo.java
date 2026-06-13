package com.PPOOII.Laboratorio.Entities;

import com.vehiclemanagement.entity.Vehicle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Check;

import java.io.Serializable;

@Entity
@Check(constraints = "estado in ('PO', 'EA', 'RO')")
@Table(name = "conductor_vehiculo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"persona_id", "vehicle_id"}, name = "uk_vehiculo_conductor")
})
public class ConductorVehiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona conductor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 2)
    private EstadoConductor estado = EstadoConductor.EA;

    public enum EstadoConductor {
        PO, EA, RO
    }

    public Long getId() {
        return id;
    }

    public Persona getConductor() {
        return conductor;
    }

    public void setConductor(Persona conductor) {
        this.conductor = conductor;
    }

    public Vehicle getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehicle vehiculo) {
        this.vehiculo = vehiculo;
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }
}
