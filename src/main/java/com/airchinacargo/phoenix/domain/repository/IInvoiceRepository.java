package com.airchinacargo.phoenix.domain.repository;

import com.airchinacargo.phoenix.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ChenYu 2018 07 19
 */
public interface IInvoiceRepository extends JpaRepository<Invoice, Integer> {
}
