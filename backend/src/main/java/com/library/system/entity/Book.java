package com.library.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal; // ⚠️ 必须导入这个，用于处理价格
import java.time.LocalDateTime;

@Data // Lombok 自动生成 Getters, Setters, ToString
@Entity // 告诉 JPA 这是一个实体类
@Table(name = "sys_book") // 对应数据库表名
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键 ID

    @Column(unique = true, nullable = false)
    private String isbn; // ISBN号

    private String title;  // 书名
    private String author; // 作者

    private String publisher; // 出版社 (虽然最近没怎么用，保留着备用)

    private Integer stock; // 库存数量

    private Integer status; // 状态 (1=正常, 0=下架)

    // ★★★ 新增字段：分类 ★★★
    // 对应数据库的 category VARCHAR(50)
    private String category;

    // ★★★ 新增字段：价格 ★★★
    // 对应数据库的 price DECIMAL(10, 2)
    // 使用 BigDecimal 是处理金钱的标准做法，避免精度丢失
    private BigDecimal price;

    @Version
    private Integer version; // 乐观锁版本号 (防止并发扣库存出问题)

    @Column(name = "create_time")
    private LocalDateTime createTime; // 入库时间

    // 在保存到数据库之前，自动填写入库时间
    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        // 如果没有设置价格，默认给个 0，防止报错
        if (this.price == null) {
            this.price = BigDecimal.ZERO;
        }
    }
}