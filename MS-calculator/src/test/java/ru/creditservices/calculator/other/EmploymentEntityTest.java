package ru.creditservices.calculator.other;

import org.junit.jupiter.api.Test;
import ru.creditservices.calculator.model.entity.EmploymentEntity;
import ru.creditservices.calculator.model.enums.EmploymentStatus;
import ru.creditservices.calculator.model.enums.Position;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmploymentEntityTest {
    @Test
    void allGettersAndBuilderCovered() {
        EmploymentEntity entity = EmploymentEntity.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .employmentINN("2223334445")
                .salary(BigDecimal.valueOf(90000))
                .position(Position.WORKER)
                .workExperienceTotal(25)
                .workExperienceCurrent(10)
                .build();
        assertEquals(EmploymentStatus.EMPLOYED, entity.getEmploymentStatus());
        assertEquals("2223334445", entity.getEmploymentINN());
        assertEquals(BigDecimal.valueOf(90000), entity.getSalary());
        assertEquals(Position.WORKER, entity.getPosition());
        assertEquals(25, entity.getWorkExperienceTotal());
        assertEquals(10, entity.getWorkExperienceCurrent());
    }
}
