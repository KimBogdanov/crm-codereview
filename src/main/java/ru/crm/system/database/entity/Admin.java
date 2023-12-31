package ru.crm.system.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.crm.system.converter.MoneyConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "userInfo")
@ToString(exclude = {"id", "orders", "logInfos", "salaryLogs"})
@Entity
public class Admin implements BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UserInfo userInfo;

    @Column(nullable = false)
    @Convert(converter = MoneyConverter.class)
    private BigDecimal shiftRate;

    @Builder.Default
    @OneToMany(mappedBy = "admin")
    private List<Order> orders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "admin")
    private List<LogInfo> logInfos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "admin")
    private List<SalaryLog> salaryLogs = new ArrayList<>();
}