package com.ladera.batchprocessing.repository;

import com.ladera.batchprocessing.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer,Integer> {
}
