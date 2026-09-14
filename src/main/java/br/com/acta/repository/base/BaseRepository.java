package br.com.acta.repository.base;

import br.com.acta.document.base.BaseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BaseRepository<ENT extends BaseDocument> extends MongoRepository<ENT, UUID> {
}
