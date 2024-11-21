package org.javers.spring.boot.redis.domain;

import java.io.Serializable;

import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Entity
@RedisHash("Device")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @org.springframework.data.annotation.Id
    private String name;

    private Sensor sensor;

    private Firmware firmware;

    public Device(String name, Sensor sensor) {
        super();
        this.name = name;
        this.sensor = sensor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Firmware getFirmware() {
        return firmware;
    }

    public void setFirmware(Firmware firmware) {
        this.firmware = firmware;
    }

    @Override
    public String toString() {
        return String.format("Device [name=%s, sensor=%s, firmware=%s]", name, sensor, firmware);
    }

}
