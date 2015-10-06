package org.microlending.app.loan.repository;

import org.microlending.app.loan.domain.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {

}
